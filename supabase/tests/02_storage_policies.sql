-- =============================================================================
-- pgTAP Tests: Storage Buckets & Visitor Photo Constraints
-- Run locally with: supabase test db
-- =============================================================================
begin;
select plan(4);

select ok(
    exists(select 1 from storage.buckets where id = 'avatars' and file_size_limit = 2097152),
    'avatars storage bucket configured with 2MB limit'
);

select ok(
    exists(select 1 from storage.buckets where id = 'visitor-photos' and file_size_limit = 5242880),
    'visitor-photos storage bucket configured with 5MB limit'
);

insert into auth.users (id, email, aud, role)
values ('33333333-3333-3333-3333-333333333333', 'photo_user@jelajahkota.id', 'authenticated', 'authenticated')
on conflict (id) do nothing;

set local role authenticated;
select set_config('request.jwt.claim.sub', '33333333-3333-3333-3333-333333333333', true);

select throws_ok(
    $$insert into public.visitor_photos (place_id, user_id, uploader_name, storage_path, image_url, mime_type, file_size_bytes)
      values ('place_jodipan', '33333333-3333-3333-3333-333333333333', 'User', 'wrong-folder/pic.jpg', 'https://example.com/pic.jpg', 'image/jpeg', 1024)$$,
    '42501',
    null,
    'RLS rejects visitor_photos insert when storage_path does not start with user_id/'
);

select throws_ok(
    $$insert into public.visitor_photos (place_id, user_id, uploader_name, storage_path, image_url, mime_type, file_size_bytes)
      values ('place_jodipan', '33333333-3333-3333-3333-333333333333', 'User', '33333333-3333-3333-3333-333333333333/pic.svg', 'https://example.com/pic.svg', 'image/svg+xml', 1024)$$,
    '23514',
    null,
    'CHECK constraint rejects disallowed MIME type image/svg+xml'
);

select * from finish();
rollback;
