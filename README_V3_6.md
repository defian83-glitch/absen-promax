# ABSEN PRO MAX V3.6 — Kiosk UI & Camera Reliability

## Perubahan
- Versi aplikasi dinaikkan ke 3.6.0 (versionCode 7).
- Area isi Mode Kiosk sekarang dapat di-scroll sehingga instruksi dan tombol tidak terpotong pada layar HP.
- Tinggi preview kamera dibuat adaptif dalam rentang 220–280 dp.
- Teks status membedakan deteksi wajah dari verifikasi identitas.
- Form nama dan divisi dibuat satu baris agar lebih ringkas.
- Fitur data pegawai, absensi manual, rekap, payroll, uang makan Rp10.000/hari hadir, dan penyimpanan lokal SharedPreferences dipertahankan.

## Status biometrik — belum boleh dianggap aktif
V3.6 ini **belum melakukan pengenalan identitas pegawai**. ML Kit Face Detection hanya mendeteksi lokasi/fitur wajah dan tracking ID sementara; itu bukan face recognition. Kode tidak mengarang hasil identitas dan tidak membuat absensi otomatis dari deteksi wajah.

Untuk implementasi biometrik yang benar dibutuhkan:
1. Model face-embedding TFLite/ONNX yang lisensi dan asalnya jelas, cocok untuk Android, serta diuji pada variasi pencahayaan/perangkat.
2. Pendaftaran beberapa sampel per pegawai dengan persetujuan tertulis/tercatat.
3. Enkripsi template di Android Keystore, pembatasan akses admin, penghapusan/retensi data, dan alternatif absensi non-biometrik.
4. Ambang pencocokan yang ditentukan dari uji FAR/FRR pada data yang representatif.
5. Liveness/anti-spoof yang diuji terhadap foto, video, dan layar; gerakan kepala saja bukan jaminan anti-spoof.

Sampai semua pengujian tersebut selesai, gunakan absensi admin/manual. Jangan aktifkan absensi otomatis hanya berdasarkan pesan “wajah terdeteksi”.

## Build
Buka folder `absen_src` di Android Studio, tunggu Gradle Sync, lalu pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
