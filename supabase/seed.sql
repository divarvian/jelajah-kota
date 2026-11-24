-- =============================================================================
-- JelajahKota — Deterministic Malang Raya & Nusantara Catalog Seed Data (DML Only)
-- Every published city has real published destinations (zero empty dead-end cities).
-- Safe to re-run idempotently via ON CONFLICT DO UPDATE.
-- =============================================================================

-- 1. Cities (Malang Raya primary focus + curated Nusantara cities with real places)
insert into public.cities (id, name, sort_order, is_published)
values
    ('malang', 'Kota Malang', 1, true),
    ('batu', 'Kota Batu', 2, true),
    ('kab_malang', 'Kab. Malang', 3, true),
    ('jogja', 'Yogyakarta', 4, true),
    ('semarang', 'Semarang', 5, true)
on conflict (id) do update
set
    name = excluded.name,
    sort_order = excluded.sort_order,
    is_published = excluded.is_published;

-- Unpublish legacy placeholder cities that currently have zero destinations
update public.cities
set is_published = false
where id in ('jkt', 'solo', 'bdg', 'sby');

-- 2. Categories
insert into public.categories (id, name, sort_order, is_published)
values
    ('cat_city', 'Wisata Kota', 1, true),
    ('cat_heritage', 'Budaya & Sejarah', 2, true),
    ('cat_mountain', 'Pegunungan', 3, true),
    ('cat_nature', 'Wisata Alam', 4, true),
    ('cat_family', 'Rekreasi Keluarga', 5, true)
on conflict (id) do update
set
    name = excluded.name,
    sort_order = excluded.sort_order,
    is_published = excluded.is_published;

-- 3. Places (Real GPS coordinates + structured durations; rating/review_count synced live by trigger)
insert into public.places (
    id, city_id, category_id, name, location, city_name, category_name,
    description, duration, min_duration_minutes, max_duration_minutes,
    ticket_price, ticket_price_amount, opening_hours, operational_days,
    latitude, longitude, rating, review_count, is_popular, is_published
)
values
    (
        'place_jodipan', 'malang', 'cat_city',
        'Kampung Warna-Warni Jodipan',
        'Jl. Juanda, Jodipan, Kec. Blimbing, Kota Malang',
        'Kota Malang', 'Wisata Kota',
        'Permukiman tepi Sungai Brantas yang disulap menjadi kanvas mural raksasa beraneka warna dengan jembatan kaca ikonik, lorong payung, dan ratusan spot foto kreatif karya mahasiswa dan seniman lokal.',
        '1-2 Jam', 60, 120,
        'Rp 5.000 / orang', 5000,
        '07.00 - 18.00 WIB', 'Setiap Hari (Senin - Minggu)',
        -7.983010, 112.637510,
        0.00, 0, true, true
    ),
    (
        'place_kayutangan', 'malang', 'cat_heritage',
        'Kayutangan Heritage',
        'Jl. Jenderal Basuki Rahmat, Klojen, Kota Malang',
        'Kota Malang', 'Budaya & Sejarah',
        'Kawasan cagar budaya Kota Malang dengan deretan bangunan bersejarah peninggalan era Hindia Belanda, kafe vintage estetik, jalur pedestrian bergaya Eropa, dan pertunjukan musik jalanan akhir pekan.',
        '2-3 Jam', 120, 180,
        'Gratis', 0,
        '24 Jam (Kafe: 08.00 - 23.00 WIB)', 'Setiap Hari (Senin - Minggu)',
        -7.977810, 112.630510,
        0.00, 0, true, true
    ),
    (
        'place_alun_tugu', 'malang', 'cat_city',
        'Alun-Alun Tugu Malang',
        'Jl. Tugu, Kiduldalem, Kec. Klojen, Kota Malang',
        'Kota Malang', 'Wisata Kota',
        'Taman lingkar terbuka hijau pusat pemerintahan Kota Malang yang dikelilingi kolam teratai indah, tugu kemerdekaan bersejarah, dan air mancur menari dengan tata cahaya warna-warni di malam hari.',
        '1-2 Jam', 60, 120,
        'Gratis', 0,
        '24 Jam', 'Setiap Hari (Senin - Minggu)',
        -7.977140, 112.634010,
        0.00, 0, false, true
    ),
    (
        'place_museum_angkut', 'batu', 'cat_family',
        'Museum Angkut Batu',
        'Jl. Terusan Sultan Agung No.2, Ngaglik, Kota Batu',
        'Kota Batu', 'Rekreasi Keluarga',
        'Museum transportasi terbesar di Asia Tenggara yang memamerkan ratusan koleksi kendaraan antik dan modern lintas zaman dengan zona foto bertema Eropa, Hollywood, hingga Pasar Apung Nusantara.',
        '2-4 Jam', 120, 240,
        'Rp 100.000 / orang', 100000,
        '12.00 - 20.00 WIB', 'Setiap Hari (Senin - Minggu)',
        -7.878950, 112.520020,
        0.00, 0, true, true
    ),
    (
        'place_alun_batu', 'batu', 'cat_family',
        'Alun-Alun Kota Wisata Batu',
        'Jl. Diponegoro, Sisir, Kec. Batu, Kota Batu',
        'Kota Batu', 'Rekreasi Keluarga',
        'Ruang publik ikonik berhawa sejuk di pusat Kota Batu dengan wahana Bianglala raksasa, taman bermain keluarga, air mancur interaktif, serta kuliner legendaris Pos Ketan Legenda.',
        '1-2 Jam', 60, 120,
        'Gratis (Bianglala Rp 5.000)', 0,
        '24 Jam', 'Setiap Hari (Senin - Minggu)',
        -7.871180, 112.526920,
        0.00, 0, false, true
    ),
    (
        'place_bromo', 'kab_malang', 'cat_mountain',
        'Gunung Bromo & Lautan Pasir',
        'Taman Nasional Bromo Tengger Semeru, Kab. Malang',
        'Kab. Malang', 'Pegunungan',
        'Salah satu kaldera terindah di dunia dengan pemandangan magis matahari terbit dari Bukit Penanjakan, lautan pasir berbisik, Pura Luhur Poten, kawah belerang aktif, dan padang savana Bukit Teletubbies.',
        '4-6 Jam', 240, 360,
        'Rp 29.000 (Domestik) / Rp 320.000 (Mancanegara)', 29000,
        '00.00 - 18.00 WIB (Sunrise Tour)', 'Setiap Hari (Senin - Minggu)',
        -7.942490, 112.953010,
        0.00, 0, true, true
    ),
    (
        'place_waterfall', 'kab_malang', 'cat_nature',
        'Coban Rondo & Taman Labirin',
        'Pandesari, Kec. Pujon, Kabupaten Malang',
        'Kab. Malang', 'Wisata Alam',
        'Air terjun alami dengan ketinggian 84 meter di lereng Gunung Panderman yang sejuk, dilengkapi taman labirin tanaman hijau, wahana outbound, perkemahan pinus, dan udara pegunungan segar.',
        '2-3 Jam', 120, 180,
        'Rp 35.000 / orang', 35000,
        '08.00 - 17.00 WIB', 'Setiap Hari (Senin - Minggu)',
        -7.884910, 112.477380,
        0.00, 0, true, true
    ),
    (
        'place_malioboro', 'jogja', 'cat_city',
        'Malioboro',
        'Sosromenduran, Gedong Tengen, Kota Yogyakarta',
        'Yogyakarta', 'Wisata Kota',
        'Kawasan jalan ikonik di jantung kota Yogyakarta dengan pedestrian rindang, deretan seniman jalanan, pertokoan khas batik, kuliner gudeg, dan suasana malam heritage tempo dulu yang memikat jutaan pelancong.',
        '2-3 Jam', 120, 180,
        'Gratis', 0,
        '24 Jam (Setiap Hari)', 'Setiap Hari (Senin - Minggu)',
        -7.792580, 110.365840,
        0.00, 0, true, true
    ),
    (
        'place_kota_lama', 'semarang', 'cat_heritage',
        'Kota Lama Semarang',
        'Tanjung Mas, Kec. Semarang Utara, Kota Semarang',
        'Semarang', 'Budaya & Sejarah',
        'Little Netherland Indonesia seluas 31 hektare dengan lebih dari 50 gedung kuno megah yang telah direstorasi, Gereja Blenduk beratap kubah perunggu, pasar seni barang antik, dan taman srigunting.',
        '2-3 Jam', 120, 180,
        'Gratis', 0,
        '08.00 - 22.00 WIB', 'Setiap Hari (Senin - Minggu)',
        -6.968250, 110.427850,
        0.00, 0, true, true
    )
on conflict (id) do update
set
    city_id = excluded.city_id,
    category_id = excluded.category_id,
    name = excluded.name,
    location = excluded.location,
    city_name = excluded.city_name,
    category_name = excluded.category_name,
    description = excluded.description,
    duration = excluded.duration,
    min_duration_minutes = excluded.min_duration_minutes,
    max_duration_minutes = excluded.max_duration_minutes,
    ticket_price = excluded.ticket_price,
    ticket_price_amount = excluded.ticket_price_amount,
    opening_hours = excluded.opening_hours,
    operational_days = excluded.operational_days,
    latitude = excluded.latitude,
    longitude = excluded.longitude,
    is_popular = excluded.is_popular,
    is_published = excluded.is_published;

-- Re-sync place ratings/review counts from actual reviews so zero fake counts remain
update public.places p
set
    review_count = coalesce(stats.cnt, 0),
    rating = coalesce(stats.avg_rating, 0.00)
from (
    select
        pl.id as place_id,
        count(r.id)::integer as cnt,
        round(avg(r.rating)::numeric, 2) as avg_rating
    from public.places pl
    left join public.reviews r on r.place_id = pl.id
    group by pl.id
) stats
where p.id = stats.place_id;
