# JelajahKota 🏙️

**JelajahKota** adalah aplikasi panduan wisata dan eksplorasi destinasi kota berbasis **native Android**. Aplikasi ini dirancang untuk memudahkan wisatawan dan warga lokal menemukan tempat menarik, kuliner khas, wisata alam, serta pengalaman budaya terbaik, dengan cakupan awal di **Kota Malang, Jawa Timur, Indonesia**.

Dibangun secara modern dengan **100% Jetpack Compose**, fondasi **Material 3 (Light & Dark Mode penuh)**, struktur **Single Module (`:app`) dengan Clean Package-by-Feature**, **MVVM + Unidirectional Data Flow (UDF)**, **Type-Safe Navigation Compose**, **Coil 3**, serta integrasi penuh **Supabase (Auth, PostgREST, Storage, RLS & pgTAP)** dengan dukungan mode ganda (online Supabase / fallback lokal offline).

---

## 📱 Fitur Utama

### 1. Welcome Splash & Onboarding (3 Slide Interaktif + Guest-First)
- **Splash Screen**: Foto senja Tugu Malang (`bg_splash_city.webp`) dengan animasi paralel wordmark *Josefin Sans Bold* dan pin peta 3D.
- **Onboarding 3 Slide**: Kolase foto lokal Kota Malang terkompresi WebP (*Jodipan*, *Alun-Alun Tugu*, *Kayutangan*, *Bromo*, *Coban Rondo*, *Kuliner Malang*) dengan tombol *Lewati* (masuk langsung sebagai Tamu), *Masuk*, dan *Daftar Akun*.
- **Navigasi Aman**: Setelah memilih *Masuk* / *Daftar Akun* dari Onboarding, layar Onboarding langsung dikeluarkan dari backstack (tidak ada tombol kembali ke Onboarding). Jika pengguna menutup aplikasi sebelum masuk atau memilih Tamu, Onboarding akan tampil lagi saat aplikasi dibuka ulang.

### 2. Autentikasi (`feature/auth`)
- Alur ringkas sesuai UX standar industri: **Masuk**, **Daftar Akun**, **Lupa Kata Sandi → Verifikasi OTP 6 Digit → Kata Sandi Baru → Konfirmasi Sukses**.
- **Guest-First Flow**: Pengunjung dapat menelusuri Beranda, Eksplorasi, Detail, Ulasan, dan Galeri tanpa login; aksi tulis (Favorit, Rencana, Ulasan, Upload Foto) memunculkan dialog `AuthRequiredDialog` rapi dan kembali otomatis ke layar asal setelah login.
- Mendukung simpan email (*Ingat Email* via DataStore), opsi *Lanjutkan sebagai Tamu* saat di root auth, serta integrasi penuh Supabase Auth.

### 3. Beranda, Eksplorasi & Detail Destinasi (`feature/places`)
- **Beranda**: Hierarki alami (`TopBar` dengan avatar monogram/tamu → `SearchBar` → *Quick Category Chips* → *Destinasi Populer* → *Jelajahi Berdasarkan Kota* Malang Raya & Nusantara non-kosong → *Rekomendasi Untukmu* + *Pull-to-Refresh*). Klik kota atau kategori langsung memfilter Beranda di tempat; *Lihat Semua* membawa filter aktif ke tab Eksplorasi.
- **Eksplorasi**: Katalog destinasi dengan pencarian, chip kota & kategori dinamis (tanpa opsi kosong), pengurutan (*Paling Populer*, *Terdekat*, *Rating Tertinggi*, *Harga Termurah*, *Nama A–Z*), badge jarak `km` saat izin lokasi aktif, *Pull-to-Refresh*, dan toggle Grid/List.
- **Detail Destinasi**: Hero image (Coil 3 + fallback lokal), info cepat, jarak dari lokasi pengguna, kartu lokasi dengan tombol **Peta** (membuka Google Maps via `geo:` intent), deskripsi expandable, galeri foto interaktif + tautan ke **Galeri Pengunjung**, tombol favorit persisten, tombol bagikan (`Intent.ACTION_SEND`), dan pratinjau ulasan.

### 4. Rencana Kunjungan (`feature/plans`)
- **Daftar Rencana**: Jadwal tersimpan lengkap dengan catatan kunjungan dan konfirmasi hapus.
- **Kalender Interaktif**: Berbasis `java.time.YearMonth` & `LocalDate` asli (navigasi bulan, blokir tanggal lampau, format Bahasa Indonesia).
- **Konfirmasi Rencana**: Rentang tanggal Type-Safe (`startEpochDay`, `endEpochDay`), stepper pengunjung, dan catatan opsional.

### 5. Rating & Ulasan (`feature/reviews`)
- Ringkasan skor rating dan distribusi bintang `5★`–`1★` asli yang tersinkronisasi otomatis ke tabel `places` melalui trigger PostgreSQL `trg_sync_place_review_stats`.
- **Tulis, Edit & Hapus Ulasan Sendiri** (ditegakkan di UI & kebijakan RLS server) serta layar khusus **Ulasan Saya** dari halaman Profil.

### 6. Galeri Foto Pengunjung (`feature/gallery`)
- Grid galeri foto per destinasi dengan **Lightbox Fullscreen**, caption, nama pengunggah, dan tanggal.
- **Android Photo Picker (`PickVisualMedia`)** dengan validasi ketat ukuran (`<= 5 MB`), MIME type, dan **magic-byte signature** (`JPG`/`PNG`/`WebP`) sebelum diunggah ke Supabase Storage (`visitor-photos/{userId}/{placeId}/...`).
- Hapus foto hanya untuk pemilik (ditegakkan di UI, RLS database, dan Storage policies).

### 7. Favorit, Profil & Pengaturan (`feature/favorites`, `feature/profile`)
- **Favorit**: Grid destinasi tersimpan dengan pencarian dan toggle favorit instan.
- **Profil & Pengaturan**: Tampilan khusus Tamu vs Login dengan avatar terpadu (`JelajahKotaUserAvatar`: foto asli → monogram inisial → ikon Tamu), statistik milik sendiri (Favorit, Rencana, Ulasan), **Edit Profil tervalidasi** (email read-only, `DatePickerDialog` tanggal lahir, enum `FilterChip` jenis kelamin, dropdown kota, validasi nomor HP & bio selaras constraint PostgreSQL), Ganti Foto Profil via Photo Picker (`<= 2 MB`, Storage bucket `avatars`), **izin lokasi opsional Just-in-Time** (`ACCESS_COARSE_LOCATION` — tidak diminta saat registrasi), Mode Gelap penuh di 100% layar, serta modal informasi *Bantuan & FAQ*, *Tentang Aplikasi*, *Kontak Dukungan*, dan *Ketentuan Layanan*.

---

## 🏗️ Arsitektur & Struktur Package (`:app`)

```text
.github/workflows/android-ci.yml                # GitHub Actions CI (unit tests, lint, debug & release R8)
.env.example                                    # Template konfigurasi Supabase (salin ke .env yang gitignored)
supabase/
├── config.toml                                 # Konfigurasi lokal Supabase CLI
├── migrations/                                 # Skema PostgreSQL, trigger, least-privilege GRANT, RLS & Storage
├── seed.sql                                    # Seed deterministik Kota Malang & Nusantara (DML only)
└── tests/                                      # Suite pgTAP untuk verifikasi RLS & Storage constraints
app/src/
├── debug/res/xml/network_security_config.xml   # Cleartext lokal 10.0.2.2/localhost (debug-only)
└── main/
    ├── assets/OFL.txt                          # Lisensi font Josefin Sans & Lato
    ├── keepRules/rules.keep                    # R8 keep rules (Serialization, Navigation, Supabase, Ktor, Coil)
    ├── res/                                    # Resource terpadu (WebP foto Malang, strings.xml, backup_rules)
    └── java/com/arvian/jelajahkota/
        ├── di/                                 # AppContainer (Dual-mode Supabase / Local DI)
        ├── navigation/                         # AppDestinations, MainShellScreen/State, NavigationExtensions, JelajahKotaAppRoot, *NavGraph
        ├── core/
        │   ├── common/                         # Result<T>, DispatcherProvider, location/ (LocationProvider)
        │   ├── designsystem/                   # theme/ (Light + Dark), component/ (Coil AsyncImage, UserAvatar, BottomNavBar, Dialogs)
        │   ├── domain/                         # model/, repository/, validation/ (ProfileValidator)
        │   └── data/                           # preferences/ (DataStore), repository/, supabase/ (DTO, Validator, Repos)
        └── feature/
            ├── onboarding/                     # Splash & Onboarding 3 slide
            ├── auth/                           # SignIn, SignUp, Forgot, OTP 6-digit, Reset
            ├── places/                         # Home, Explore, PlaceDetail
            ├── plans/                          # PlansList, DatePicker, PlanConfirmation
            ├── reviews/                        # ReviewsScreen & MyReviewsScreen (Edit/Delete own)
            ├── gallery/                        # PlaceGalleryScreen (Grid, Lightbox, Photo Picker Upload)
            ├── favorites/                      # Daftar destinasi favorit
            └── profile/                        # Profile, EditProfile, AvatarUpload (Photo Picker), Settings
```

---

## 🛠️ Tech Stack & Library

- **Bahasa & Platform**: Kotlin `2.2.10` | Native Android (`minSdk = 26`, `compileSdk = 37`, `targetSdk = 37`)
- **UI Toolkit**: Jetpack Compose (Compose BOM `2026.02.01`) + Material 3 (Full Dark Mode)
- **Image Loading**: Coil 3 (`3.1.0` — `coil-compose` + `coil-network-ktor3`)
- **Build Tool**: Gradle `9.6.0` Kotlin DSL + Android Gradle Plugin `9.4.1` + R8 Minify & Resource Shrinking (`release`)
- **Arsitektur State**: MVVM + Unidirectional Data Flow (UDF), `StateFlow`, `collectAsStateWithLifecycle`
- **Penyimpanan Lokal**: AndroidX Preferences DataStore `1.1.3`
- **Navigasi**: AndroidX Navigation Compose `2.10.2` (Type-Safe Serialization Routes)
- **Backend / Database**: Supabase Kotlin SDK `3.1.1` (PostgREST, Auth, Storage) via Ktor Android Engine `3.1.1` + Supabase CLI
- **Testing & CI**: JUnit 4, Kotlinx Coroutines Test, Compose UI Testing, pgTAP, GitHub Actions

---

## ⚙️ Konfigurasi Supabase (`.env`)

1. Salin `.env.example` menjadi `.env` di root proyek (`.env` sudah masuk `.gitignore`):
   ```env
   SUPABASE_URL=https://<project-ref>.supabase.co
   SUPABASE_PUBLISHABLE_KEY=sb_publishable_...
   ```
   > **Keamanan:** Hanya masukkan URL dan **publishable key**. Jangan pernah menaruh `service_role` / secret key di `.env` atau kode Android.
2. Jika `.env` kosong/tidak ada, aplikasi otomatis berjalan aman menggunakan repository demo lokal (offline fallback).
3. Untuk mendorong migrasi & seed ke proyek Supabase yang sudah terhubung:
   ```bash
   # Verifikasi lokal (membutuhkan Docker/Podman aktif)
   supabase start
   supabase db reset
   supabase test db

   # Push migrasi & seed ke proyek remote yang sudah di-link (jalankan setelah review)
   supabase db push --remote --include-seed --dry-run
   supabase db push --remote --include-seed
   ```

---

## 🚀 Perintah Gradle Utama

```bash
# Unit Tests + Android Lint + Debug APK + Kompilasi Compose UI Tests
./gradlew testDebugUnitTest lintDebug assembleDebug compileDebugAndroidTestKotlin

# Install APK teroptimasi R8 langsung ke perangkat fisik (tanpa uninstall debug)
./gradlew installBenchmark

# Release APK (dengan R8 Minification & Resource Shrinking)
./gradlew assembleRelease
```

---

## 📄 Lisensi Tipografi

Aplikasi ini menggunakan font **Josefin Sans** dan **Lato** di bawah lisensi [SIL Open Font License (OFL)](http://scripts.sil.org/OFL), disertakan dalam `app/src/main/assets/OFL.txt`.
