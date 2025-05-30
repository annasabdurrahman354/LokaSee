# LokaSee

LokaSee adalah aplikasi Android yang dirancang sebagai platform jual beli properti, menghubungkan penjual properti dengan calon pembeli. Pengguna dapat menjelajahi, mencari, dan melihat detail properti pada peta, sementara penjual dapat mengelola daftar properti mereka. Proyek ini dikembangkan sebagai *capstone project* untuk program Bangkit.

## Fitur Utama

* **Autentikasi Pengguna:** Sistem login dan registrasi untuk pengguna.
* **Penjelajahan Properti:**
    * Tampilan daftar properti di halaman utama.
    * Visualisasi properti pada peta interaktif (Google Maps).
    * Tampilan detail untuk setiap properti.
* **Pencarian Properti:** Fungsi pencarian berdasarkan kata kunci.
* **Filter Properti:**
    * Berdasarkan Lokasi: Provinsi, Kabupaten/Kota, Kecamatan.
    * Berdasarkan Harga: Rentang harga minimum dan maksimum.
    * Berdasarkan Luas Tanah: Rentang luas minimum dan maksimum.
* **Fitur Penjual:**
    * Membuat, memperbarui, dan menghapus daftar properti.
    * Mengunggah banyak gambar untuk setiap properti.
    * Menandai lokasi properti di peta.
* **Profil Pengguna:** Pengguna dapat melihat dan mengelola informasi profil mereka.
* **Tampilan Responsif:** Desain antarmuka yang menyesuaikan dengan berbagai ukuran layar.

## Teknologi yang Digunakan

### Aplikasi Android (Frontend)
* **Bahasa Pemrograman:** Kotlin
* **Arsitektur:** MVVM (Model-View-ViewModel), Clean Architecture
* **UI:** XML Layouts, Material Components
    * Carousel Gambar: `org.imaginativeworld.whynotimagecarousel.ImageCarousel`
* **Navigasi:** Android Navigation Component
* **Networking:** Retrofit, OkHttp, Gson
* **Penyimpanan Lokal:** DataStore (untuk preferensi pengguna dan token)
* **Peta:** Google Maps SDK
* **Asynchronous Programming:** Coroutines, LiveData
* **Build Tool:** Gradle

### Backend
* **Framework:** Laravel
* **Bahasa Pemrograman:** PHP
* **Database:** MySQL

### Cloud
* Google Cloud Platform (Hosting Backend)
* Firebase

## Struktur Proyek (Modul Aplikasi)

* `data/`: Berisi model data (Post, User, Provinsi, dll.), repositori, konfigurasi jaringan (Retrofit, ApiService), dan penyimpanan data lokal (DataStore Preferences, AppPreferences).
* `di/`: Pengaturan Dependency Injection.
* `ui/`:
    * `auth/`: Aktivitas dan Fragmen untuk autentikasi (Login, Register, Onboarding).
    * `main/`: Aktivitas utama dan Fragmen untuk fitur inti (Home, Map, Search, Post, Profile, Seller).
    * `customview/`: Komponen UI kustom seperti `EmailEditText` dan `PasswordEditText`.
    * `adapter/`: Adapter untuk RecyclerView (misalnya, `PostListAdapter`, `SellerPostListAdapter`).
* `util/`: Kelas utilitas dan fungsi ekstensi (misalnya, untuk pemrosesan gambar, helper view, konfigurasi API).
* `res/`:
    * `layout/`: Berkas XML untuk tata letak antarmuka.
    * `drawable/`: Aset grafis (ikon, background).
    * `values/`: String, warna, dimensi, tema, dan gaya.
    * `navigation/`: Graf navigasi untuk Auth dan Main.
    * `menu/`: Definisi menu untuk Bottom App Bar.

## Pengaturan dan Instalasi

1.  Clone repositori ini.
2.  Buka proyek menggunakan Android Studio.
3.  Pastikan backend API berjalan dan dapat diakses pada URL yang disebutkan di atas.
4.  Konfigurasi Google Maps API Key:
    * Kunci API sudah tersedia dalam `app/src/main/AndroidManifest.xml`. Jika perlu diganti, lakukan di sana.
5.  Build dan jalankan aplikasi pada emulator atau perangkat fisik.

*(Pertimbangkan untuk menambahkan tangkapan layar atau GIF di sini untuk menampilkan UI aplikasi)*
