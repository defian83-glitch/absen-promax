# ABSEN PROMAX V2

Pengembangan dari ABSEN PRO V1. Data absensi dan pegawai lama tetap dibaca dari SharedPreferences yang sama.

## Fitur
- Maksimal 80 pegawai dan beberapa divisi.
- Absensi manual: Hadir, Terlambat, Izin, Sakit, Alpa, Cuti, Libur.
- Jam kerja acuan 08:00–17:00.
- Rekap absensi dan riwayat.
- Pengaturan skema gaji per pegawai: Bulanan, Harian, atau Kombinasi.
- Uang makan Rp10.000 per hari hadir (Hadir/Terlambat).
- Estimasi potongan keterlambatan per menit dan Alpa per hari.
- Insentif bulanan dan lembur berdasarkan jam pulang setelah 17:00.
- Data lama tetap kompatibel; nilai penggajian pegawai lama dimulai dari nol dan dapat diatur.

## Rumus estimasi penggajian bulanan
- Bulanan: gaji pokok bulanan.
- Harian: upah harian × jumlah hari Hadir/Terlambat.
- Kombinasi: gaji bulanan + (upah harian × hari hadir).
- Tambahan: uang makan × hari hadir + insentif bulanan + lembur.
- Potongan: menit terlambat × tarif potongan/menit + Alpa × potongan/hari.

Catatan: ini estimasi berdasarkan catatan bulan berjalan. Aturan prorata gaji bulanan, hari libur, pembulatan lembur, dan kebijakan potongan perlu disesuaikan dengan ketentuan perusahaan. Data masih tersimpan lokal pada satu perangkat.

## Build APK lewat GitHub Actions
1. Upload seluruh isi ZIP ini ke repository GitHub yang digunakan untuk ABSEN PRO.
2. Commit/push ke branch `main`.
3. Buka tab **Actions** dan tunggu workflow **Build ABSEN PRO APK** selesai.
4. Unduh artifact **ABSEN-PRO-debug-apk**, lalu instal APK di HP.
