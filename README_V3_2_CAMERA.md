# Absen Pro Max V3.2 – Camera Preview

Perubahan dari V3.1:
- Menambahkan permission CAMERA.
- Menambahkan dependensi CameraX.
- Mode Kiosk meminta izin kamera saat dibuka.
- Menampilkan preview kamera depan melalui CameraX.
- Fitur data pegawai, riwayat, gaji, dan tunjangan makan dari V3.1 dipertahankan.

Batasan penting:
- Ini baru preview kamera depan, belum face detection, face enrollment, face matching, atau anti-spoofing.
- Jangan gunakan preview ini sebagai bukti identitas atau dasar keputusan kepegawaian.
- Uji pada beberapa merek/perangkat Android dan pastikan izin kamera diberikan.
- Build APK di Android Studio/CI untuk memverifikasi kompatibilitas SDK dan dependency.
