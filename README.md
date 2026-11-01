# ABSEN PRO MAX V3.1 — Multi-Device Android

Project dasar V3 dibuat dari source V2 dengan mempertahankan penyimpanan lokal kompatibel:
- SharedPreferences: `absen_pro`
- Keys: `employees` dan `records`

## Fitur yang sudah ada
- Data pegawai maksimal 80 orang dan divisi
- Input absensi manual, rekap, dan estimasi gaji
- Tunjangan makan Rp10.000 per hari hadir
- Skema gaji bulanan, harian, kombinasi, potongan, insentif, dan lembur
- Pilihan Mode Admin dan kerangka Mode Kiosk
- Tema Clean Modern putih/biru

## Tahap berikutnya
1. CameraX preview dan deteksi wajah ML Kit.
2. Pendaftaran template wajah per pegawai dan pencocokan lokal.
3. Liveness / anti-foto, pengujian lintas perangkat, dan kontrol admin.

Catatan: V3.1 belum melakukan pengenalan identitas wajah. Deteksi wajah bukan pengenalan nama. Sebelum mengaktifkan biometrik, sediakan pemberitahuan dan persetujuan/kebijakan yang sesuai, batasi akses, enkripsi template biometrik, tetapkan masa simpan, dan sediakan metode absensi alternatif.

## Build APK
Workflow GitHub Actions tersedia di `.github/workflows/build.yml` dan membangun `app-debug.apk` saat push ke `main` atau dijalankan manual.
