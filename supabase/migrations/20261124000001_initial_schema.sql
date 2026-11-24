-- =============================================================================
-- JelajahKota — Initial PostgreSQL Schema Migration
-- Covers all 5 core ownership areas:
--   1. Places directory (cities, categories, places, place_photos)
--   2. Reviews and ratings (reviews)
--   3. Favorites (favorites)
--   4. Visit plans (visit_plans)
--   5. Visitor photo gallery (visitor_photos)
--   + User profiles synced with auth.users (profiles)
-- =============================================================================

create extension if not exists pgcrypto;

-- -----------------------------------------------------------------------------
-- Shared timestamp trigger
-- -----------------------------------------------------------------------------
create or replace function public.set_updated_at()
returns trigger
language plpgsql
set search_path = public
as $$
begin
    new.updated_at = timezone('utc', now());
    return new;
end;
$$;

-- -----------------------------------------------------------------------------
-- 1. Profiles (1:1 with auth.users)
-- -----------------------------------------------------------------------------
create table if not exists public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    full_name text not null default '',
    email text not null default '',
    phone text not null default '',
    city text not null default 'Kota Malang',
    bio text not null default '',
    birth_date text not null default '',
    gender text not null default '',
    avatar_url text,
    created_at timestamptz not null default timezone('utc', now()),
    updated_at timestamptz not null default timezone('utc', now())
);

drop trigger if exists trg_profiles_updated_at on public.profiles;
create trigger trg_profiles_updated_at
    before update on public.profiles
    for each row execute function public.set_updated_at();

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
    insert into public.profiles (id, full_name, email)
    values (
        new.id,
        coalesce(
            nullif(trim(new.raw_user_meta_data ->> 'full_name'), ''),
            split_part(coalesce(new.email, 'Penjelajah'), '@', 1)
        ),
        coalesce(new.email, '')
    )
    on conflict (id) do update
    set
        email = excluded.email,
        full_name = case
            when public.profiles.full_name = '' then excluded.full_name
            else public.profiles.full_name
        end;
    return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
    after insert on auth.users
    for each row execute function public.handle_new_user();

-- -----------------------------------------------------------------------------
-- 2. Places Directory (Admin-managed catalogue)
-- -----------------------------------------------------------------------------
create table if not exists public.cities (
    id text primary key,
    name text not null unique,
    image_url text,
    sort_order integer not null default 0,
    is_published boolean not null default true,
    created_at timestamptz not null default timezone('utc', now())
);

create table if not exists public.categories (
    id text primary key,
    name text not null unique,
    icon_url text,
    sort_order integer not null default 0,
    is_published boolean not null default true,
    created_at timestamptz not null default timezone('utc', now())
);

create table if not exists public.places (
    id text primary key,
    city_id text not null references public.cities (id) on delete restrict,
    category_id text not null references public.categories (id) on delete restrict,
    name text not null,
    location text not null,
    city_name text not null default 'Malang',
    category_name text not null default '',
    description text not null default '',
    duration text not null default '1-2 Jam',
    ticket_price text not null default 'Gratis',
    ticket_price_amount integer not null default 0 check (ticket_price_amount >= 0),
    opening_hours text not null default '07.00 - 18.00 WIB',
    operational_days text not null default 'Setiap Hari (Senin - Minggu)',
    image_url text,
    rating numeric(3, 2) not null default 4.50 check (rating >= 0 and rating <= 5),
    review_count integer not null default 0 check (review_count >= 0),
    is_popular boolean not null default false,
    is_published boolean not null default true,
    created_at timestamptz not null default timezone('utc', now()),
    updated_at timestamptz not null default timezone('utc', now())
);

create index if not exists idx_places_city_id on public.places (city_id);
create index if not exists idx_places_category_id on public.places (category_id);
create index if not exists idx_places_published_popular on public.places (is_published, is_popular);

drop trigger if exists trg_places_updated_at on public.places;
create trigger trg_places_updated_at
    before update on public.places
    for each row execute function public.set_updated_at();

create table if not exists public.place_photos (
    id uuid primary key default gen_random_uuid(),
    place_id text not null references public.places (id) on delete cascade,
    image_url text not null,
    sort_order integer not null default 0,
    created_at timestamptz not null default timezone('utc', now())
);

create index if not exists idx_place_photos_place_id on public.place_photos (place_id, sort_order);

-- -----------------------------------------------------------------------------
-- 3. Reviews & Ratings (User-owned CRUD, public read)
-- -----------------------------------------------------------------------------
create table if not exists public.reviews (
    id uuid primary key default gen_random_uuid(),
    place_id text not null references public.places (id) on delete cascade,
    user_id uuid not null references auth.users (id) on delete cascade,
    author_name text not null default 'Penjelajah',
    author_avatar_url text,
    rating numeric(2, 1) not null check (rating >= 1.0 and rating <= 5.0),
    content text not null check (length(trim(content)) > 0 and length(content) <= 2000),
    created_at timestamptz not null default timezone('utc', now()),
    updated_at timestamptz not null default timezone('utc', now())
);

create index if not exists idx_reviews_place_id_created on public.reviews (place_id, created_at desc);
create index if not exists idx_reviews_user_id_created on public.reviews (user_id, created_at desc);

drop trigger if exists trg_reviews_updated_at on public.reviews;
create trigger trg_reviews_updated_at
    before update on public.reviews
    for each row execute function public.set_updated_at();

-- -----------------------------------------------------------------------------
-- 4. Favorites (Strict user-owned private table)
-- -----------------------------------------------------------------------------
create table if not exists public.favorites (
    user_id uuid not null references auth.users (id) on delete cascade,
    place_id text not null references public.places (id) on delete cascade,
    created_at timestamptz not null default timezone('utc', now()),
    primary key (user_id, place_id)
);

create index if not exists idx_favorites_user_id on public.favorites (user_id, created_at desc);

-- -----------------------------------------------------------------------------
-- 5. Visit Plans (Strict user-owned private table)
-- -----------------------------------------------------------------------------
create table if not exists public.visit_plans (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users (id) on delete cascade,
    place_id text not null references public.places (id) on delete cascade,
    place_name text not null,
    place_location text not null default '',
    start_date date not null,
    end_date date not null,
    visitor_count integer not null default 1 check (visitor_count between 1 and 50),
    notes text not null default '' check (length(notes) <= 1000),
    created_at timestamptz not null default timezone('utc', now()),
    updated_at timestamptz not null default timezone('utc', now()),
    constraint chk_visit_plans_date_order check (end_date >= start_date)
);

create index if not exists idx_visit_plans_user_id on public.visit_plans (user_id, created_at desc);

drop trigger if exists trg_visit_plans_updated_at on public.visit_plans;
create trigger trg_visit_plans_updated_at
    before update on public.visit_plans
    for each row execute function public.set_updated_at();

-- -----------------------------------------------------------------------------
-- 6. Visitor Photo Gallery (Feature #5: Public read, user-owned insert/delete)
-- -----------------------------------------------------------------------------
create table if not exists public.visitor_photos (
    id uuid primary key default gen_random_uuid(),
    place_id text not null references public.places (id) on delete cascade,
    user_id uuid not null references auth.users (id) on delete cascade,
    uploader_name text not null default 'Penjelajah',
    storage_path text not null unique,
    image_url text not null,
    caption text not null default '' check (length(caption) <= 300),
    mime_type text not null check (mime_type in ('image/jpeg', 'image/png', 'image/webp')),
    file_size_bytes integer not null check (file_size_bytes > 0 and file_size_bytes <= 5242880),
    created_at timestamptz not null default timezone('utc', now())
);

create index if not exists idx_visitor_photos_place_id on public.visitor_photos (place_id, created_at desc);
create index if not exists idx_visitor_photos_user_id on public.visitor_photos (user_id, created_at desc);
