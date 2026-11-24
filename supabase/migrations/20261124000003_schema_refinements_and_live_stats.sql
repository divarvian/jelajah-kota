-- =============================================================================
-- JelajahKota — Schema Refinements, Strict Profile Constraints, Place Coordinates,
-- Live Review Rating Aggregates Trigger, & Catalog Media Storage Bucket
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Strict Profiles Schema (DATE birth_date, enum gender, phone regex, optional city)
-- -----------------------------------------------------------------------------
alter table public.profiles
    alter column city set default '';

-- Convert birth_date safely from text -> nullable date (ISO YYYY-MM-DD)
do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'profiles'
          and column_name = 'birth_date'
          and data_type <> 'date'
    ) then
        alter table public.profiles add column if not exists birth_date_tmp date;

        update public.profiles
        set birth_date_tmp = case
            when trim(coalesce(birth_date, '')) ~ '^\d{4}-\d{2}-\d{2}$'
                then to_date(trim(birth_date), 'YYYY-MM-DD')
            when trim(coalesce(birth_date, '')) ~ '^\d{2}/\d{2}/\d{4}$'
                then to_date(trim(birth_date), 'DD/MM/YYYY')
            else null
        end;

        -- Clamp any impossible legacy dates to null before applying CHECK constraint
        update public.profiles
        set birth_date_tmp = null
        where birth_date_tmp is not null
          and (birth_date_tmp < date '1900-01-01' or birth_date_tmp > current_date);

        alter table public.profiles drop column birth_date;
        alter table public.profiles rename column birth_date_tmp to birth_date;
    end if;
end $$;

-- Normalize gender to strict nullable enum codes ('male', 'female', 'prefer_not_to_say')
alter table public.profiles alter column gender drop not null;
alter table public.profiles alter column gender drop default;

update public.profiles
set gender = case
    when lower(trim(coalesce(gender, ''))) in ('male', 'laki-laki', 'pria', 'l') then 'male'
    when lower(trim(coalesce(gender, ''))) in ('female', 'perempuan', 'wanita', 'p') then 'female'
    when lower(trim(coalesce(gender, ''))) in ('prefer_not_to_say', 'tidak disebutkan', 'memilih tidak menyebutkan', 'lainnya') then 'prefer_not_to_say'
    else null
end;

-- Normalize phone formatting (strip spaces/hyphens/parentheses; convert 08.. -> +628..)
update public.profiles
set phone = regexp_replace(coalesce(phone, ''), '[\s\-\(\)]+', '', 'g');

update public.profiles
set phone = '+62' || substring(phone from 2)
where phone ~ '^0[0-9]{8,14}$';

update public.profiles
set phone = ''
where phone <> '' and phone !~ '^\+?[0-9]{9,15}$';

-- Trim over-length legacy text safely before adding constraints
update public.profiles set full_name = left(trim(full_name), 80) where length(full_name) > 80;
update public.profiles set bio = left(bio, 300) where length(bio) > 300;
update public.profiles set city = left(trim(city), 80) where length(city) > 80;

alter table public.profiles drop constraint if exists chk_profiles_full_name_len;
alter table public.profiles add constraint chk_profiles_full_name_len
    check (length(full_name) <= 80);

alter table public.profiles drop constraint if exists chk_profiles_city_len;
alter table public.profiles add constraint chk_profiles_city_len
    check (length(city) <= 80);

alter table public.profiles drop constraint if exists chk_profiles_bio_len;
alter table public.profiles add constraint chk_profiles_bio_len
    check (length(bio) <= 300);

alter table public.profiles drop constraint if exists chk_profiles_phone_format;
alter table public.profiles add constraint chk_profiles_phone_format
    check (phone = '' or phone ~ '^\+?[0-9]{9,15}$');

alter table public.profiles drop constraint if exists chk_profiles_birth_date_range;
alter table public.profiles add constraint chk_profiles_birth_date_range
    check (birth_date is null or (birth_date >= date '1900-01-01' and birth_date <= current_date));

alter table public.profiles drop constraint if exists chk_profiles_gender_enum;
alter table public.profiles add constraint chk_profiles_gender_enum
    check (gender is null or gender in ('male', 'female', 'prefer_not_to_say'));

-- Update new-user trigger so newly registered users do not get forced city/gender/birth_date
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
    insert into public.profiles (id, full_name, email, city, birth_date, gender)
    values (
        new.id,
        left(
            coalesce(
                nullif(trim(new.raw_user_meta_data ->> 'full_name'), ''),
                split_part(coalesce(new.email, 'Penjelajah'), '@', 1)
            ),
            80
        ),
        coalesce(new.email, ''),
        '',
        null,
        null
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

-- -----------------------------------------------------------------------------
-- 2. Places Coordinates & Structured Visit Duration (Non-Hardcoded Metadata)
-- -----------------------------------------------------------------------------
alter table public.places
    add column if not exists latitude double precision,
    add column if not exists longitude double precision,
    add column if not exists min_duration_minutes integer,
    add column if not exists max_duration_minutes integer;

alter table public.places drop constraint if exists chk_places_lat_range;
alter table public.places add constraint chk_places_lat_range
    check (latitude is null or (latitude >= -90 and latitude <= 90));

alter table public.places drop constraint if exists chk_places_lng_range;
alter table public.places add constraint chk_places_lng_range
    check (longitude is null or (longitude >= -180 and longitude <= 180));

alter table public.places drop constraint if exists chk_places_min_duration_pos;
alter table public.places add constraint chk_places_min_duration_pos
    check (min_duration_minutes is null or min_duration_minutes > 0);

alter table public.places drop constraint if exists chk_places_max_duration_pos;
alter table public.places add constraint chk_places_max_duration_pos
    check (max_duration_minutes is null or max_duration_minutes > 0);

alter table public.places drop constraint if exists chk_places_duration_order;
alter table public.places add constraint chk_places_duration_order
    check (
        min_duration_minutes is null
        or max_duration_minutes is null
        or max_duration_minutes >= min_duration_minutes
    );

-- Default new places to 0.00 rating & 0 reviews until real reviews arrive
alter table public.places alter column rating set default 0.00;

-- -----------------------------------------------------------------------------
-- 3. Automatic Live Rating & Review-Count Sync Trigger on public.reviews
-- -----------------------------------------------------------------------------
create or replace function public.sync_place_review_stats()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
    target_place_id text;
begin
    target_place_id := coalesce(new.place_id, old.place_id);

    update public.places p
    set
        review_count = coalesce(stats.cnt, 0),
        rating = coalesce(stats.avg_rating, 0.00)
    from (
        select
            count(*)::integer as cnt,
            round(avg(r.rating)::numeric, 2) as avg_rating
        from public.reviews r
        where r.place_id = target_place_id
    ) stats
    where p.id = target_place_id;

    if tg_op = 'UPDATE' and old.place_id is distinct from new.place_id then
        update public.places p
        set
            review_count = coalesce(stats.cnt, 0),
            rating = coalesce(stats.avg_rating, 0.00)
        from (
            select
                count(*)::integer as cnt,
                round(avg(r.rating)::numeric, 2) as avg_rating
            from public.reviews r
            where r.place_id = old.place_id
        ) stats
        where p.id = old.place_id;
    end if;

    return null;
end;
$$;

drop trigger if exists trg_sync_place_review_stats on public.reviews;
create trigger trg_sync_place_review_stats
    after insert or update or delete on public.reviews
    for each row execute function public.sync_place_review_stats();

-- -----------------------------------------------------------------------------
-- 4. Admin-Managed Public-Read Storage Bucket for Catalog Media
-- -----------------------------------------------------------------------------
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('catalog-media', 'catalog-media', true, 5242880, array['image/jpeg', 'image/png', 'image/webp'])
on conflict (id) do update
set
    public = excluded.public,
    file_size_limit = excluded.file_size_limit,
    allowed_mime_types = excluded.allowed_mime_types;

drop policy if exists "catalog_media_public_read" on storage.objects;
create policy "catalog_media_public_read"
    on storage.objects for select
    to anon, authenticated
    using (bucket_id = 'catalog-media');
