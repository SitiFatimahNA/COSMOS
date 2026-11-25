# 🌌 COSMOS — Space Explorer

> **Discover the Universe** — Aplikasi Android eksplorasi luar angkasa berbasis NASA Open APIs

---

## 📱 Deskripsi

COSMOS adalah aplikasi Android bertema eksplorasi luar angkasa yang memungkinkan pengguna menjelajahi berbagai informasi astronomi secara real-time menggunakan NASA Open APIs. Mulai dari foto astronomi harian, misi penjelajahan Mars, hingga pemantauan asteroid yang mendekati Bumi — semua tersedia dalam satu aplikasi dengan desain modern dan futuristik.

---

## 🎯 Tujuan

- Menyediakan platform eksplorasi luar angkasa yang informatif dan mudah digunakan
- Menampilkan data astronomi real-time dari NASA kepada pengguna umum
- Menjadi media edukasi astronomi yang menarik dan interaktif

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
| 🌠 **APOD** | Astronomy Picture of the Day — gambar astronomi harian beserta penjelasan ilmiahnya |
| 🔴 **Mars Rover** | Foto langsung dari rover Curiosity, Opportunity, dan Spirit di Mars |
| ☄️ **Near Earth Objects** | Pemantauan asteroid dan objek dekat Bumi dari NASA NeoWs |
| 🪐 **Planet 3D** | Model planet interaktif dengan efek pencahayaan 3D, gyroscope, dan drag gesture |
| ⭐ **Favorites** | Simpan konten favorit secara persisten dengan DataStore |
| 🔭 **NASA Gallery** | Galeri gambar astronomi dari NASA Images API |
| ⚙️ **Settings** | Tema (Light/Dark/System), bahasa (ID/EN), dan informasi aplikasi |

---

## 🛠️ Teknologi

```
Language        : Kotlin
UI Framework    : Jetpack Compose + Material 3
Architecture    : MVVM + Repository Pattern
Navigation      : Navigation Compose
State Management: StateFlow + collectAsStateWithLifecycle
Networking      : Retrofit 2 + Gson
Image Loading   : Coil 2
Local Storage   : DataStore Preferences
Animations      : Compose Animation (fade, slide, shimmer)
3D Rendering    : Canvas API (custom shader simulation)
Sensor          : Gyroscope (SensorManager)
```

---

## 🚀 NASA APIs yang Digunakan

| API | Endpoint | Keterangan |
|-----|----------|------------|
| APOD | `api.nasa.gov/planetary/apod` | Gambar astronomi harian |
| Mars Rover Photos | `api.nasa.gov/mars-photos/api/v1/rovers/` | Foto rover Mars |
| NeoWs | `api.nasa.gov/neo/rest/v1/feed` | Asteroid dekat Bumi |
| NASA Images | `images-api.nasa.gov/search` | Galeri gambar NASA |

Dokumentasi resmi: [https://api.nasa.gov/](https://api.nasa.gov/)

---

## 📦 Cara Instalasi & Menjalankan

### Prasyarat
- Android Studio Ladybug atau lebih baru
- JDK 11+
- Android SDK API 24+

### Langkah-langkah

```bash
# 1. Clone repository
git clone https://github.com/SitiFatimahNA/COSMOS.git
cd COSMOS

# 2. Konfigurasi API Key (lihat bagian bawah)

# 3. Buka di Android Studio, tunggu Gradle sync selesai

# 4. Jalankan di emulator atau perangkat fisik
#    Run → Run 'app'
```

---

## 🔑 Konfigurasi API Key

**WAJIB:** API Key harus dikonfigurasi sebelum aplikasi dapat mengambil data NASA.

### Cara mendapatkan API Key (gratis):
1. Kunjungi [https://api.nasa.gov/](https://api.nasa.gov/)
2. Isi form "Generate API Key" (hanya butuh nama & email)
3. Cek email untuk mendapatkan API key

### Cara menambahkan ke project:

Buka atau buat file `local.properties` di root project (jangan di-commit ke GitHub):

```properties
# local.properties
sdk.dir=C\:\\Users\\NamaKamu\\AppData\\Local\\Android\\Sdk

# Tambahkan baris ini dengan API key kamu:
NASA_API_KEY=MASUKKAN_API_KEY_DI_SINI
```

> ⚠️ File `local.properties` sudah ada di `.gitignore` — API Key tidak akan ter-upload ke GitHub.

> 💡 Jika tidak dikonfigurasi, aplikasi otomatis menggunakan `DEMO_KEY` yang memiliki batas **30 request/jam** dan **50 request/hari**.

---

## 📁 Struktur Folder

```
app/src/main/
├── java/com/siti/cosmos/
│   ├── data/
│   │   ├── local/
│   │   │   └── PreferencesDataStore.kt    # DataStore: favorites, theme, language
│   │   ├── model/
│   │   │   ├── ApodModel.kt               # Model data APOD
│   │   │   ├── MarsModel.kt               # Model data Mars Rover
│   │   │   ├── NeoModel.kt                # Model data Near Earth Objects
│   │   │   ├── FavoriteItem.kt            # Model favorit
│   │   │   └── NasaResponse.kt            # Model NASA Images API
│   │   ├── remote/
│   │   │   ├── NasaApiService.kt          # Interface Retrofit (APOD, Mars, NEO)
│   │   │   ├── NasaApi.kt                 # Interface NASA Images API
│   │   │   └── RetrofitClient.kt          # OkHttp + Retrofit setup
│   │   └── repository/
│   │       ├── ApodRepository.kt          # Logic & error handling APOD
│   │       ├── MarsRepository.kt          # Logic & error handling Mars
│   │       ├── NeoRepository.kt           # Logic & error handling NEO
│   │       └── NasaRepository.kt          # NASA Images API
│   ├── ui/
│   │   ├── components/
│   │   │   └── CommonComponents.kt        # Komponen reusable (shimmer, error, empty)
│   │   ├── navigation/
│   │   │   ├── NavRoutes.kt               # Definisi semua route
│   │   │   └── AppNavigation.kt           # NavHost + bottom navigation
│   │   ├── screens/
│   │   │   ├── SplashScreen.kt
│   │   │   ├── HomeScreen.kt
│   │   │   ├── ApodScreen.kt
│   │   │   ├── MarsScreen.kt
│   │   │   ├── NeoScreen.kt
│   │   │   ├── PlanetsScreen.kt
│   │   │   ├── PlanetDetailScreen.kt      # Planet 3D interaktif
│   │   │   ├── FavoritesScreen.kt
│   │   │   └── SettingsScreen.kt
│   │   ├── theme/
│   │   │   ├── Color.kt                   # Dark & Light palette
│   │   │   ├── Theme.kt                   # MaterialTheme configuration
│   │   │   └── Type.kt                    # Typography
│   │   └── viewmodel/
│   │       ├── ApodViewModel.kt
│   │       ├── MarsViewModel.kt
│   │       ├── NeoViewModel.kt
│   │       ├── FavoritesViewModel.kt
│   │       └── SettingsViewModel.kt
│   ├── ExploreScreen.kt                   # Grid planet (tab Explore)
│   ├── NasaGallery.kt                     # NASA Images gallery component
│   ├── Planet3DView.kt                    # Canvas 3D planet renderer
│   └── MainActivity.kt                    # Entry point
└── res/
    ├── values/strings.xml                 # Bahasa Indonesia
    ├── values-en/strings.xml              # English
    └── drawable-nodpi/                    # Gambar planet HD
```

---

## 🌐 Multi Bahasa

Aplikasi mendukung **Bahasa Indonesia** dan **Bahasa Inggris**:

- Ganti bahasa melalui menu **Settings → Bahasa**
- Pilihan bahasa tersimpan secara persisten
- Semua teks UI, pesan error, empty state, dan label tersedia dalam kedua bahasa
- Konten dari NASA (judul foto, deskripsi) tetap dalam bahasa aslinya (Inggris)

---

## 🎨 Light Mode & Dark Mode

| Mode | Deskripsi |
|------|-----------|
| **Light** | Background terang, cocok untuk penggunaan siang hari |
| **Dark** | Background bernuansa luar angkasa (hitam pekat), cocok untuk malam hari |
| **System** | Mengikuti preferensi sistem Android secara otomatis |

Ganti tema melalui **Settings → Tema Tampilan**. Pilihan tersimpan dan berlaku saat aplikasi dibuka kembali.

---

## 📐 Responsive Design

- Layout menggunakan `LazyColumn` / `LazyVerticalGrid` yang auto-scroll
- `BoxWithConstraints` untuk penyesuaian landscape vs portrait jika diperlukan
- `configChanges` di AndroidManifest mencegah activity restart saat rotasi
- State tersimpan via `rememberSaveable` dan ViewModel

---

## 🔧 Versi & Kompatibilitas

| Komponen | Versi |
|----------|-------|
| compileSdk | 36 |
| minSdk | 24 (Android 7.0 Nougat) |
| targetSdk | 36 |
| Kotlin | 2.0.21 |
| Compose BOM | 2024.09.00 |
| AGP | 8.12.3 |

---

## ⚠️ Catatan Penting

- **API Key**: Jangan pernah meng-commit `local.properties` atau API Key ke repository publik
- **DEMO_KEY**: Cocok untuk pengujian awal, tapi terbatas 30 req/jam — gunakan key asli untuk demo
- **Koneksi internet**: Diperlukan untuk mengambil data NASA; ada error handling untuk kondisi offline
- **Planet 3D**: Efek 3D menggunakan Canvas API (tidak memerlukan library tambahan)

---

## 👩‍💻 Dibuat oleh

**Siti Fatimah NA**
Project Pemrograman Mobile — Eksplorasi Luar Angkasa

*Powered by [NASA Open APIs](https://api.nasa.gov/)*
