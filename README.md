# ABSEN PRO

Project Android baru untuk aplikasi absensi pegawai.

## Fitur awal
- Data pegawai hingga 80 orang (nama dan divisi).
- Catat status: Hadir, Terlambat, Izin, Sakit, Alpa, Cuti, Libur.
- Jam masuk dan pulang.
- Ringkasan jumlah status untuk data yang tersimpan.
- Data disimpan lokal di perangkat (SharedPreferences).

## Build APK lewat GitHub Actions
1. Upload seluruh isi ZIP ini ke repository GitHub baru/kosong.
2. Buka tab **Actions**.
3. Pilih workflow **Build ABSEN PRO APK** lalu jalankan, atau lakukan push ke branch `main`.
4. Setelah sukses, buka hasil workflow dan unduh artifact **ABSEN-PRO-debug-apk**.

Catatan: versi awal ini menyimpan data di satu perangkat dan belum memiliki login, sinkronisasi antar-HP, backup cloud, atau penggajian otomatis.
