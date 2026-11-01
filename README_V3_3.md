# ABSEN PRO MAX V3.3

Pembaruan dari source V3.2 yang dikirim:
- Tampilan header dan halaman dirapikan dengan gaya modern biru-putih.
- Versi aplikasi dinaikkan menjadi 3.3.0.
- Mode Kiosk menggunakan CameraX + ML Kit Face Detection untuk mendeteksi jumlah wajah secara real-time.
- Pemrosesan frame dilakukan lokal di perangkat.
- Penyimpanan pegawai dan absensi lama tetap memakai SharedPreferences `absen_pro` dengan key `employees` dan `records`.

Batasan yang harus dipahami: deteksi wajah belum mengenali identitas pegawai. Pendaftaran foto/template wajah, pencocokan identitas, dan liveness/anti-spoof belum diimplementasikan. Build APK perlu dijalankan melalui Android Studio atau GitHub Actions.
