# ABSEN PRO MAX V3.5 — Face Detection Foundation

V3.5 adalah pembaruan fondasi kamera/deteksi, bukan rilis pengenalan identitas wajah otomatis.

## Perubahan dari V3.4
- Versi aplikasi menjadi 3.5.0 (versionCode 6).
- ML Kit Face Detector memakai batas ukuran wajah minimum 12% dari area gambar untuk mengurangi deteksi wajah yang terlalu kecil.
- Mengaktifkan tracking ID ML Kit untuk melacak wajah antar-frame. Tracking ID bukan identitas pegawai dan tidak boleh dipakai sebagai pencocokan orang.
- Pesan Mode Kiosk diperjelas agar pengguna tidak menganggap deteksi sebagai verifikasi identitas.
- Fitur data pegawai, absensi manual, rekap, payroll, uang makan Rp10.000/hari hadir, CameraX, dan tema dashboard V3.4 dipertahankan.

## Belum tersedia (jangan dianggap aktif)
- Pendaftaran wajah per pegawai.
- Ekstraksi embedding biometrik dan pencocokan identitas.
- Liveness / anti-spoof foto atau video.
- Absensi otomatis berdasarkan identitas.

Fitur-fitur di atas membutuhkan model embedding yang tervalidasi, penyimpanan template yang terenkripsi, persetujuan/pemberitahuan pegawai, masa retensi, kontrol akses, metode alternatif, serta pengujian FAR/FRR dan spoofing sebelum dipakai di kantor.

## Build APK
Buka folder `absen_src` di Android Studio, tunggu Gradle Sync, lalu pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
Atau gunakan workflow GitHub Actions yang tersedia di `.github/workflows/build.yml`.
