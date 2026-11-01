# ABSEN PRO MAX V3.4

Pembaruan dari source V3.3:
- Dashboard Admin diperbarui dengan header navy, kartu panel kontrol, dan ringkasan jumlah pegawai, kehadiran hari ini, serta catatan hari ini.
- Identitas aplikasi dinaikkan ke versi 3.4.0 (versionCode 5) agar tidak tertukar dengan APK V3.3.
- Fitur absensi, data pegawai, rekap, payroll, uang makan Rp10.000 per hari hadir, CameraX, dan ML Kit dipertahankan.

## Batasan fitur wajah
ML Kit pada versi ini hanya mendeteksi jumlah/keberadaan wajah. Belum ada pendaftaran template wajah, pencocokan identitas pegawai, maupun liveness/anti-spoof. Jangan gunakan deteksi ini sebagai verifikasi identitas.

## Build
Buka folder project di Android Studio, tunggu Gradle Sync selesai, lalu pilih Build > Build Bundle(s) / APK(s) > Build APK(s).
