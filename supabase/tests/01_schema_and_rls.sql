-- =============================================================================
-- pgTAP Tests: Schema, Constraints, Live Triggers, Grants & Row-Level Security
-- Run locally with: supabase test db
-- =============================================================================
begin;
select plan(22);

-- 1. Core tables exist
select has_table('public', 'profiles', 'profiles table exists');
select has_table('public', 'cities', 'cities table exists');
select has_table('public', 'categories', 'categories table exists');
select has_table('public', 'places', 'places table exists');
select has_table('public', 'reviews', 'reviews table exists');
select has_table('public', 'favorites', 'favorites table exists');
select has_table('public', 'visit_plans', 'visit_plans table exists');
select has_table('public', 'visitor_photos', 'visitor_photos table exists');

-- 2. RLS enabled on all public tables
select is(
    (select count(*)::int from pg_tables where schemaname = 'public' and rowsecurity = false),
    0,
    'RLS is enabled on all tables in public schema'
);

-- 3. Seeded users for ownership & constraint tests
insert into auth.users (id, email, raw_user_meta_data, aud, role)
values
    ('11111111-1111-1111-1111-111111111111', 'user_a@jelajahkota.id', '{"full_name":"User A"}'::jsonb, 'authenticated', 'authenticated'),
    ('22222222-2222-2222-2222-222222222222', 'user_b@jelajahkota.id', '{"full_name":"User B"}'::jsonb, 'authenticated', 'authenticated')
on conflict (id) do nothing;

-- 3b. Profile schema constraints reject invalid gender, phone, and future birth_date
select is(
    (select city from public.profiles where id = '11111111-1111-1111-1111-111111111111'),
    '',
    'new profile does not force a default city at signup'
);
select throws_ok(
    $$update public.profiles set gender = 'invalid_value' where id = '11111111-1111-1111-1111-111111111111'$$,
    '23514',
    null,
    'profiles rejects invalid gender value'
);
select throws_ok(
    $$update public.profiles set phone = 'not-a-phone' where id = '11111111-1111-1111-1111-111111111111'$$,
    '23514',
    null,
    'profiles rejects non-numeric phone value'
);
select throws_ok(
    $$update public.profiles set birth_date = date '2099-01-01' where id = '11111111-1111-1111-1111-111111111111'$$,
    '23514',
    null,
    'profiles rejects future birth_date'
);

-- 4. Anon can read published places, cannot insert places or read private visit_plans
set local role anon;
select ok(
    (select count(*) >= 5 from public.places),
    'anon can read published Malang places'
);
select throws_ok(
    $$insert into public.places (id, city_id, category_id, name, location) values ('hack', 'malang', 'cat_city', 'Hack', 'Malang')$$,
    '42501',
    null,
    'anon cannot insert places'
);
select throws_ok(
    $$select * from public.visit_plans$$,
    '42501',
    null,
    'anon has no SELECT privilege on private visit_plans'
);
reset role;

-- 5. User A can insert own favorite, plan, and review (and live trigger syncs place rating/count)
set local role authenticated;
select set_config('request.jwt.claim.sub', '11111111-1111-1111-1111-111111111111', true);

insert into public.favorites (user_id, place_id)
values ('11111111-1111-1111-1111-111111111111', 'place_jodipan');

insert into public.visit_plans (user_id, place_id, place_name, place_location, start_date, end_date, visitor_count, notes)
values ('11111111-1111-1111-1111-111111111111', 'place_jodipan', 'Kampung Warna-Warni Jodipan', 'Kota Malang', '2026-12-01', '2026-12-02', 2, 'Pagi hari');

insert into public.reviews (id, place_id, user_id, author_name, rating, content)
values ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'place_jodipan', '11111111-1111-1111-1111-111111111111', 'User A', 5.0, 'Sangat keren!');

select is((select count(*)::int from public.favorites), 1, 'User A sees own favorite');
select is(
    (select review_count from public.places where id = 'place_jodipan'),
    1,
    'review insert trigger updates places.review_count live'
);
select is(
    (select rating from public.places where id = 'place_jodipan'),
    5.00::numeric(3, 2),
    'review insert trigger updates places.rating live'
);

-- 6. User B cannot see User A private favorites/plans and cannot update/delete User A review
select set_config('request.jwt.claim.sub', '22222222-2222-2222-2222-222222222222', true);

select is((select count(*)::int from public.favorites), 0, 'User B cannot read User A favorites');
select is((select count(*)::int from public.visit_plans), 0, 'User B cannot read User A visit plans');

update public.reviews set content = 'Hijacked!' where id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';
select is(
    (select content from public.reviews where id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    'Sangat keren!',
    'User B cannot update User A review'
);

select * from finish();
rollback;
