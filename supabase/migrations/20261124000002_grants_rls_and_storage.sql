-- =============================================================================
-- JelajahKota — Least-Privilege Grants, Row-Level Security (RLS) & Storage Policies
-- =============================================================================

-- 1. Least-privilege explicit SQL grants (RLS does NOT replace grants)
revoke all on all tables in schema public from anon, authenticated;

-- Public catalogue read grants
grant select on public.cities to anon, authenticated;
grant select on public.categories to anon, authenticated;
grant select on public.places to anon, authenticated;
grant select on public.place_photos to anon, authenticated;

-- Public-read / owner-write social tables
grant select on public.reviews to anon, authenticated;
grant insert, update, delete on public.reviews to authenticated;

grant select on public.visitor_photos to anon, authenticated;
grant insert, delete on public.visitor_photos to authenticated;

-- Private user-owned tables
grant select, insert, update on public.profiles to authenticated;
grant select, insert, delete on public.favorites to authenticated;
grant select, insert, update, delete on public.visit_plans to authenticated;

-- 2. Enable RLS on EVERY exposed table
alter table public.profiles enable row level security;
alter table public.cities enable row level security;
alter table public.categories enable row level security;
alter table public.places enable row level security;
alter table public.place_photos enable row level security;
alter table public.reviews enable row level security;
alter table public.favorites enable row level security;
alter table public.visit_plans enable row level security;
alter table public.visitor_photos enable row level security;

-- -----------------------------------------------------------------------------
-- Catalogue Policies (published rows readable by anon + authenticated)
-- -----------------------------------------------------------------------------
drop policy if exists "cities_select_published" on public.cities;
create policy "cities_select_published"
    on public.cities for select
    to anon, authenticated
    using (is_published = true);

drop policy if exists "categories_select_published" on public.categories;
create policy "categories_select_published"
    on public.categories for select
    to anon, authenticated
    using (is_published = true);

drop policy if exists "places_select_published" on public.places;
create policy "places_select_published"
    on public.places for select
    to anon, authenticated
    using (is_published = true);

drop policy if exists "place_photos_select_published" on public.place_photos;
create policy "place_photos_select_published"
    on public.place_photos for select
    to anon, authenticated
    using (
        exists (
            select 1 from public.places p
            where p.id = place_id and p.is_published = true
        )
    );

-- -----------------------------------------------------------------------------
-- Profiles Policies (strictly self-managed)
-- -----------------------------------------------------------------------------
drop policy if exists "profiles_select_own" on public.profiles;
create policy "profiles_select_own"
    on public.profiles for select
    to authenticated
    using ((select auth.uid()) = id);

drop policy if exists "profiles_insert_own" on public.profiles;
create policy "profiles_insert_own"
    on public.profiles for insert
    to authenticated
    with check ((select auth.uid()) = id);

drop policy if exists "profiles_update_own" on public.profiles;
create policy "profiles_update_own"
    on public.profiles for update
    to authenticated
    using ((select auth.uid()) = id)
    with check ((select auth.uid()) = id);

-- -----------------------------------------------------------------------------
-- Reviews Policies (public read; authenticated users CRUD own reviews only)
-- -----------------------------------------------------------------------------
drop policy if exists "reviews_select_public" on public.reviews;
create policy "reviews_select_public"
    on public.reviews for select
    to anon, authenticated
    using (true);

drop policy if exists "reviews_insert_own" on public.reviews;
create policy "reviews_insert_own"
    on public.reviews for insert
    to authenticated
    with check ((select auth.uid()) = user_id);

drop policy if exists "reviews_update_own" on public.reviews;
create policy "reviews_update_own"
    on public.reviews for update
    to authenticated
    using ((select auth.uid()) = user_id)
    with check ((select auth.uid()) = user_id);

drop policy if exists "reviews_delete_own" on public.reviews;
create policy "reviews_delete_own"
    on public.reviews for delete
    to authenticated
    using ((select auth.uid()) = user_id);

-- -----------------------------------------------------------------------------
-- Favorites Policies (strictly private to owner)
-- -----------------------------------------------------------------------------
drop policy if exists "favorites_select_own" on public.favorites;
create policy "favorites_select_own"
    on public.favorites for select
    to authenticated
    using ((select auth.uid()) = user_id);

drop policy if exists "favorites_insert_own" on public.favorites;
create policy "favorites_insert_own"
    on public.favorites for insert
    to authenticated
    with check ((select auth.uid()) = user_id);

drop policy if exists "favorites_delete_own" on public.favorites;
create policy "favorites_delete_own"
    on public.favorites for delete
    to authenticated
    using ((select auth.uid()) = user_id);

-- -----------------------------------------------------------------------------
-- Visit Plans Policies (strictly private to owner)
-- -----------------------------------------------------------------------------
drop policy if exists "visit_plans_select_own" on public.visit_plans;
create policy "visit_plans_select_own"
    on public.visit_plans for select
    to authenticated
    using ((select auth.uid()) = user_id);

drop policy if exists "visit_plans_insert_own" on public.visit_plans;
create policy "visit_plans_insert_own"
    on public.visit_plans for insert
    to authenticated
    with check ((select auth.uid()) = user_id);

drop policy if exists "visit_plans_update_own" on public.visit_plans;
create policy "visit_plans_update_own"
    on public.visit_plans for update
    to authenticated
    using ((select auth.uid()) = user_id)
    with check ((select auth.uid()) = user_id);

drop policy if exists "visit_plans_delete_own" on public.visit_plans;
create policy "visit_plans_delete_own"
    on public.visit_plans for delete
    to authenticated
    using ((select auth.uid()) = user_id);

-- -----------------------------------------------------------------------------
-- Visitor Photos Policies (public read; authenticated owner insert/delete)
-- -----------------------------------------------------------------------------
drop policy if exists "visitor_photos_select_public" on public.visitor_photos;
create policy "visitor_photos_select_public"
    on public.visitor_photos for select
    to anon, authenticated
    using (true);

drop policy if exists "visitor_photos_insert_own" on public.visitor_photos;
create policy "visitor_photos_insert_own"
    on public.visitor_photos for insert
    to authenticated
    with check (
        (select auth.uid()) = user_id
        and storage_path like ((select auth.uid())::text || '/%')
    );

drop policy if exists "visitor_photos_delete_own" on public.visitor_photos;
create policy "visitor_photos_delete_own"
    on public.visitor_photos for delete
    to authenticated
    using ((select auth.uid()) = user_id);

-- -----------------------------------------------------------------------------
-- Storage Buckets & Owner-Folder Object Policies
-- -----------------------------------------------------------------------------
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values
    ('avatars', 'avatars', true, 2097152, array['image/jpeg', 'image/png', 'image/webp']),
    ('visitor-photos', 'visitor-photos', true, 5242880, array['image/jpeg', 'image/png', 'image/webp'])
on conflict (id) do update
set
    public = excluded.public,
    file_size_limit = excluded.file_size_limit,
    allowed_mime_types = excluded.allowed_mime_types;

drop policy if exists "avatars_public_read" on storage.objects;
create policy "avatars_public_read"
    on storage.objects for select
    to anon, authenticated
    using (bucket_id = 'avatars');

drop policy if exists "avatars_owner_insert" on storage.objects;
create policy "avatars_owner_insert"
    on storage.objects for insert
    to authenticated
    with check (
        bucket_id = 'avatars'
        and (storage.foldername(name))[1] = (select auth.uid())::text
    );

drop policy if exists "avatars_owner_update" on storage.objects;
create policy "avatars_owner_update"
    on storage.objects for update
    to authenticated
    using (
        bucket_id = 'avatars'
        and (storage.foldername(name))[1] = (select auth.uid())::text
    )
    with check (
        bucket_id = 'avatars'
        and (storage.foldername(name))[1] = (select auth.uid())::text
    );

drop policy if exists "avatars_owner_delete" on storage.objects;
create policy "avatars_owner_delete"
    on storage.objects for delete
    to authenticated
    using (
        bucket_id = 'avatars'
        and (storage.foldername(name))[1] = (select auth.uid())::text
    );

drop policy if exists "visitor_photos_storage_public_read" on storage.objects;
create policy "visitor_photos_storage_public_read"
    on storage.objects for select
    to anon, authenticated
    using (bucket_id = 'visitor-photos');

drop policy if exists "visitor_photos_storage_owner_insert" on storage.objects;
create policy "visitor_photos_storage_owner_insert"
    on storage.objects for insert
    to authenticated
    with check (
        bucket_id = 'visitor-photos'
        and (storage.foldername(name))[1] = (select auth.uid())::text
    );

drop policy if exists "visitor_photos_storage_owner_delete" on storage.objects;
create policy "visitor_photos_storage_owner_delete"
    on storage.objects for delete
    to authenticated
    using (
        bucket_id = 'visitor-photos'
        and (storage.foldername(name))[1] = (select auth.uid())::text
    );
