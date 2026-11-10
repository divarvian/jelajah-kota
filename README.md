# JelajahKota 🏙️

**JelajahKota** adalah aplikasi panduan wisata dan eksplorasi destinasi kota berbasis **native Android**. Aplikasi ini dirancang untuk memudahkan wisatawan dan warga lokal menemukan tempat menarik, kuliner khas, wisata alam, serta pengalaman budaya terbaik, dengan cakupan awal di **Kota Malang, Jawa Timur, Indonesia**.

Dibangun secara modern dengan **100% Jetpack Compose**, fondasi **Material 3**, arsitektur **Multi-Module**, **MVVM + Unidirectional Data Flow (UDF)**, serta terintegrasi dengan **Supabase**.

---

## 📱 Fitur Utama

### 1. Welcome Splash Screen
- Tampilan pembuka beranimasi dengan latar visual kota dan gradasi warna biru khas JelajahKota (`#002E66` ke `#005BBF`).
- Tipografi judul *Josefin Sans Bold* dan ilustrasi pin peta 3D.
- Navigasi otomatis menuju Onboarding saat aplikasi pertama kali dijalankan.

### 2. Onboarding Flow (3 Slide Interaktif)
- **Slide 1 — Jelajahi Destinasi Kotamu!**: Menampilkan kolase foto Kampung Warna-Warni Jodipan, Alun-Alun Tugu, dan Kayutangan Heritage.
- **Slide 2 — Rencanakan Kunjunganmu**: Inspirasi petualangan alam Gunung Bromo, Coban Waterfall, dan kafe heritage.
- **Slide 3 — Bagikan Pengalamanmu**: Pengalaman kuliner legendaris Bakso Malang, pemandangan malam kota, dan galeri foto pengunjung.
- **Indikator Halaman & Aksi Cepat**: Dilengkapi indikator 3 baris aktif, tombol *Lewati*, *Masuk*, dan *Daftar Akun*.
- **DataStore Persistence**: Status penyelesaian onboarding disimpan secara asinkron menggunakan *AndroidX Preferences DataStore*. Pada pembukaan aplikasi berikutnya, pengguna langsung diarahkan ke Beranda/Dashboard.

### 3. Beranda / Dashboard
- **Top Bar Profil**: Menampilkan avatar pengguna, sapaan *"Hai, [Nama Pengguna]"*, tombol pengaturan, serta ikon notifikasi dengan badge penghitung.
- **Headline Eksplorasi**: Tipografi *"Eksplorasi Destinasi Sudut Kotamu !"* dengan aksen warna oranye khas.
- **Search Bar & Filter**: Kolom pencarian cepat bergaya pill dengan tombol filter kustom.
- **Pilihan Kota (*"Ingin berkunjung kemana?"*)**: Baris gulir horizontal berisi avatar destinasi kota (Yogyakarta, Jakarta, Solo, Bandung, Surabaya, dll).
- **Destinasi Populer**: Kartu carousel destinasi berukuran 200x200dp dengan sudut lengkung 30dp, pin lokasi, informasi daerah, dan rating bintang ulasan (4.6).
- **Kategori Destinasi**: Tombol pill kategori wisata alam seperti *Beach* dan *Mountain*.
- **Bottom Navigation Bar**: 5 menu utama (*Beranda*, *Eksplorasi*, *Jadwalkan*, *Favorit*, *Profile*) yang telah disesuaikan dengan *native WindowInsets* / gesture bar Android.

---

## 🏗️ Arsitektur & Struktur Modul

Proyek ini menerapkan arsitektur **Clean Multi-Module** untuk menjaga modularitas, isolasi dependensi, kecepatan kompilasi, serta kemudahan pengujian:

```text
       ┌───────────┐
       │   :app    │ (MainActivity, single-activity Compose, root NavHost)
       └─┬───┬───┬─┘
         │   │   │
         │   │   └────────────────┐
         ▼   ▼                    ▼
┌──────────────┐ ┌──────────────┐ ┌────────────────┐
│:feature:on-  │ │:feature:auth │ │:feature:places │ (HomeRoute, HomeViewModel,
│  boarding    │ └───────┬──────┘ └───────┬────────┘  HomeScreen & komponen)
└───────┬──────┘         │                │
        ├────────────────┴────────────────┼────────┐
        ▼                                 ▼        ▼
┌───────────────┐                  ┌───────────────┐
│ :core:domain  │                  │:core:design-  │ (Design tokens, drawables,
└───────▲───────┘                  │  system       │  komponen UI, font)
        │                          └───────────────┘
┌───────┴───────┐
│  :core:data   │ (Repository, DataStore, Supabase client provider)
└───────┬───────┘
        ▼
┌───────────────┐
│ :core:common  │ (Result<T>, DispatcherProvider, utilitas bersama)
└───────────────┘
```

### Tanggung Jawab Modul:
- **`:app`**: Titik masuk aplikasi, `Application` class, dan konfigurasi navigasi utama.
- **`:core:common`**: Tipe data dasar seperti `Result<T>` dan abstraksi coroutine dispatchers.
- **`:core:designsystem`**: Token warna, tipografi (*Josefin Sans* dan *Lato*), tema Material 3, dan vektor drawable.
- **`:core:domain`**: Model bisnis (`Place`, `CityDestination`, `CategoryItem`, `UserProfile`) dan antarmuka repositori.
- **`:core:data`**: Implementasi repositori (`PlacesRepositoryImpl`, `UserPreferencesRepositoryImpl`) dan klien Supabase.
- **`:feature:onboarding`**: Layar splash custom dan alur pager onboarding 3 slide.
- **`:feature:auth`**: Layar typed placeholder untuk alur masuk dan pendaftaran.
- **`:feature:places`**: Layar Beranda/Dashboard lengkap dengan ViewModel dan komponen modular.

---

## 🛠️ Tech Stack & Library

- **Bahasa & Platform**: Kotlin 2.2.10 | Native Android (Min SDK 26, Target/Compile SDK 37)
- **UI Toolkit**: Jetpack Compose (Compose BOM 2026.02.01) + Material 3
- **Build Tool**: Gradle 9.6.0 Kotlin DSL + Android Gradle Plugin 9.4.1
- **Manajemen Dependensi**: Gradle Version Catalog (`gradle/libs.versions.toml`)
- **Arsitektur State**: Unidirectional Data Flow (UDF), ViewModel, `StateFlow`, `collectAsStateWithLifecycle`
- **Penyimpanan Lokal**: AndroidX Preferences DataStore 1.1.3
- **Navigasi**: AndroidX Navigation Compose 2.10.2
- **Backend / Database**: Supabase Kotlin SDK 3.1.1 (PostgREST, Auth, Storage) via Ktor Android Engine 3.1.1
- **Testing**: JUnit 4, Kotlinx Coroutines Test, Compose UI Testing

---

## 🚀 Memulai & Menjalankan Proyek

### Prasyarat:
1. **JDK 17** atau lebih baru (direkomendasikan JDK 21 / 25).
2. **Android SDK** terpasang dengan Platform Android 37 (`compileSdk = 37`).
3. **Android Studio** (versi Ladybug / Meerkat atau yang lebih baru).

### Perintah Gradle Utama:

```bash
# 1. Menampilkan seluruh modul yang terdaftar
./gradlew projects

# 2. Menjalankan seluruh pengujian unit (Unit Tests)
./gradlew test

# 3. Membangun file APK Debug
./gradlew assembleDebug

# 4. Menjalankan analisis kualitas kode (Android Lint)
./gradlew lintDebug
```

File APK yang berhasil dibangun akan berada di:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🗄️ Backend Supabase (Lokal)

Proyek ini telah dilengkapi dengan konfigurasi Supabase CLI untuk pengembangan lokal di folder `supabase/`:
- `supabase/config.toml`: Konfigurasi layanan lokal (API, Database, Auth, Storage).
- `supabase/migrations/`: Riwayat migrasi skema database PostgreSQL.
- `supabase/seed.sql`: Data awal (*seed data*) deterministik.

Untuk menjalankan Supabase secara lokal menggunakan CLI:
```bash
supabase start
```

---

## 📄 Lisensi Tipografi

Aplikasi ini menggunakan font **Josefin Sans** dan **Lato** yang didistribusikan di bawah lisensi resmi [SIL Open Font License (OFL)](http://scripts.sil.org/OFL).
Salinan lisensi disertakan dalam `core/designsystem/src/main/assets/OFL.txt`.
