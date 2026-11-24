# JelajahKota 🏙️

**JelajahKota** adalah aplikasi panduan wisata dan eksplorasi destinasi kota berbasis **native Android**. Aplikasi ini dirancang untuk memudahkan wisatawan dan warga lokal menemukan tempat menarik, kuliner khas, wisata alam, serta pengalaman budaya terbaik, dengan cakupan awal di **Kota Malang, Jawa Timur, Indonesia**.

Dibangun secara modern dengan **100% Jetpack Compose**, fondasi **Material 3**, struktur **Single Module (`:app`) dengan Clean Package-by-Feature**, **MVVM + Unidirectional Data Flow (UDF)**, **Type-Safe Navigation Compose**, serta fondasi backend **Supabase**.

---

## 📱 Fitur Utama

### 1. Welcome Splash & Onboarding (3 Slide Interaktif)
- **Splash Screen**: Latar visual kota dengan gradasi biru JelajahKota (`#002E66` → `#005BBF`), animasi paralel wordmark *Josefin Sans Bold*, dan pin peta 3D.
- **Onboarding 3 Slide**: *Jelajahi Destinasi Sudut Kotamu!*, *Rencanakan Kunjunganmu*, dan *Bagikan Pengalamanmu* lengkap dengan indikator halaman, tombol *Lewati*, *Masuk*, dan *Daftar Akun*.
- **DataStore Persistence**: Status onboarding hanya ditandai selesai saat pengguna menekan *Lewati* atau berhasil *Masuk/Daftar*, dan `startDestination` dikunci saat startup agar backstack navigasi stabil.

### 2. Autentikasi (`feature/auth`)
- Alur lengkap: **Masuk**, **Daftar Akun**, **Lupa Kata Sandi**, **Konfirmasi Email**, **Verifikasi OTP**, **Kata Sandi Baru**, dan **Konfirmasi Sukses**.
- Validasi format email, kebijakan kekuatan kata sandi, filter digit & dukungan paste OTP, serta sinkronisasi sesi pengguna tunggal.

### 3. Beranda, Eksplorasi & Detail Destinasi (`feature/places`)
- **Beranda**: Sapaan profil pengguna tersinkronisasi, pencarian cepat, pilihan kota, destinasi populer, dan kategori wisata yang langsung terhubung ke layar Eksplorasi dengan filter awal.
- **Eksplorasi**: Katalog destinasi dengan pencarian, chip kota & kategori dinamis dari repository, pengurutan (*Paling Populer*, *Rating Tertinggi*, *Harga Termurah*, *Nama A–Z*), dan toggle tampilan Grid/List.
- **Detail Destinasi**: Hero image, info cepat (rating, durasi, tiket, jam buka), deskripsi expandable, galeri foto, tombol favorit persisten (`FavoritesRepository`), tombol bagikan (`Intent.ACTION_SEND`), dan pratinjau ulasan dinamis.

### 4. Rencana Kunjungan (`feature/plans`)
- **Daftar Rencana**: Menampilkan jadwal tersimpan lengkap dengan catatan kunjungan dan dialog konfirmasi hapus.
- **Kalender Interaktif**: Berbasis `java.time.YearMonth` & `LocalDate` asli (navigasi antar bulan, grid 5–6 baris dinamis, blokir tanggal lampau, nama hari/bulan Bahasa Indonesia).
- **Konfirmasi Rencana**: Menerima rentang tanggal secara Type-Safe (`startEpochDay`, `endEpochDay`), stepper jumlah pengunjung, serta kolom catatan kunjungan opsional.

### 5. Rating & Ulasan (`feature/reviews`)
- Ringkasan skor rating dan breakdown metrik wisata yang dihitung dinamis per destinasi.
- Pencarian ulasan, lampiran foto ulasan, serta dialog **Tulis Ulasan** (rating 1–5 bintang + isi ulasan).

### 6. Favorit & Profil (`feature/favorites`, `feature/profile`)
- **Favorit**: Grid destinasi tersimpan dengan pencarian dan toggle hapus favorit instan.
- **Profil & Pengaturan**: Statistik dinamis (Favorit, Rencana, Ulasan), ubah profil lengkap (nama, telepon, bio, kota, tanggal lahir, jenis kelamin), unggah avatar, serta preferensi aplikasi (Lokasi, Mode Gelap, Notifikasi) yang tersimpan persisten di DataStore.

---

## 🏗️ Arsitektur & Struktur Package (`:app`)

Proyek dikonsolidasikan ke **1 modul `:app`** untuk menjaga kecepatan build, kesederhanaan konfigurasi Gradle, dan kemudahan refactor, dengan tetap mempertahankan disiplin **Clean Package-by-Feature**:

```text
app/src/
├── debug/res/xml/network_security_config.xml   # Cleartext lokal 10.0.2.2/localhost (debug-only)
└── main/
    ├── assets/OFL.txt                          # Lisensi font Josefin Sans & Lato
    ├── res/                                    # Resource terpadu (WebP terkompresi < 1 MB)
    └── java/com/arvian/jelajahkota/
        ├── JelajahKotaApp.kt
        ├── MainActivity.kt
        ├── di/                                 # AppContainer (DI terpusat)
        ├── navigation/                         # AppDestinations, JelajahKotaAppRoot, *NavGraph.kt
        ├── core/
        │   ├── common/                         # Result<T>, DispatcherProvider
        │   ├── designsystem/                   # theme/ (Light + Dark), component/
        │   ├── domain/                         # model/, repository/ (kontrak suspend & Flow)
        │   └── data/                           # preferences/ (DataStore), repository/, supabase/
        └── feature/
            ├── onboarding/                     # Splash & Onboarding 3 slide
            ├── auth/                           # SignIn, SignUp, Forgot, OTP, Reset
            ├── places/                         # Home, Explore, PlaceDetail
            ├── plans/                          # PlansList, DatePicker, PlanConfirmation
            ├── reviews/                        # Rating, ulasan, & dialog Tulis Ulasan
            ├── favorites/                      # Daftar destinasi favorit
            └── profile/                        # Profile, EditProfile, AvatarUpload, Settings
```

---

## 🛠️ Tech Stack & Library

- **Bahasa & Platform**: Kotlin `2.2.10` | Native Android (`minSdk = 26`, `compileSdk = 37`, `targetSdk = 37`)
- **UI Toolkit**: Jetpack Compose (Compose BOM `2026.02.01`) + Material 3
- **Build Tool**: Gradle `9.6.0` Kotlin DSL + Android Gradle Plugin `9.4.1` + Version Catalog (`gradle/libs.versions.toml`)
- **Arsitektur State**: MVVM + Unidirectional Data Flow (UDF), `StateFlow`, `collectAsStateWithLifecycle`
- **Penyimpanan Lokal**: AndroidX Preferences DataStore `1.1.3`
- **Navigasi**: AndroidX Navigation Compose `2.10.2` (Type-Safe Serialization Routes)
- **Backend / Database**: Supabase Kotlin SDK `3.1.1` (PostgREST, Auth, Storage) via Ktor Android Engine `3.1.1` + Supabase CLI (`supabase/`)
- **Testing**: JUnit 4, Kotlinx Coroutines Test, Compose UI Testing

---

## 🚀 Memulai & Menjalankan Proyek

### Prasyarat:
1. **JDK 25** (sesuai `gradle/gradle-daemon-jvm.properties`).
2. **Android SDK** dengan Platform Android 37 (`compileSdk = 37`).
3. **Android Studio** versi terbaru yang mendukung AGP 9.x.

### Perintah Gradle Utama:

```bash
# 1. Memastikan hanya modul :app yang aktif
./gradlew projects

# 2. Menjalankan seluruh Unit Test
./gradlew testDebugUnitTest

# 3. Menjalankan analisis Android Lint
./gradlew lintDebug

# 4. Membangun APK Debug
./gradlew assembleDebug
```

File APK hasil build: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🗄️ Backend Supabase (Lokal)

Konfigurasi Supabase CLI tersedia di folder `supabase/`:
- `supabase/config.toml`: Konfigurasi layanan lokal (API, PostgreSQL, Auth, Storage).
- `supabase/migrations/`: Skema migrasi database PostgreSQL (tahap integrasi backend berikutnya).
- `supabase/seed.sql`: Data awal deterministik.

---

## 📄 Lisensi Tipografi

Aplikasi ini menggunakan font **Josefin Sans** dan **Lato** di bawah lisensi [SIL Open Font License (OFL)](http://scripts.sil.org/OFL), disertakan dalam `app/src/main/assets/OFL.txt`.
