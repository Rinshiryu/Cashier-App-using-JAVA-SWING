# Cashier App using Java Swing

Aplikasi kasir berbasis **Java Swing** yang terhubung dengan **MySQL** dan dibangun menggunakan **Maven**.  
Aplikasi ini memiliki sistem login dengan role **Admin** dan **User**, serta fitur diskon otomatis untuk user.

---

## 📌 Fitur Utama
- Login menggunakan username & password
- Role pengguna:
  - **Admin** → Dashboard admin
  - **User** → Diskon otomatis 3% saat pembelian
- Tampilan kasir (tampil beli / kasir)
- Sidebar navigasi seperti aplikasi modern
- Layout rapi menggunakan Java Swing
- Resource gambar (icon) terstruktur dengan Maven

---

## 🛠️ Teknologi yang Digunakan
- Java (Swing)
- Maven
- MySQL
- NetBeans IDE
- JDBC (MySQL Connector)

---


---

## 🗄️ Struktur Database (Contoh)
Tabel `user`:

| Field     | Type    |
|----------|---------|
| id       | int     |
| username | varchar |
| password | varchar |
| role     | varchar |

---

## ▶️ Cara Menjalankan Aplikasi
1. Clone repository ini
2. Import project sebagai **Maven Project** di NetBeans
3. Konfigurasikan database MySQL dan file `koneksi.java`
4. Pastikan MySQL Connector sudah terpasang di `pom.xml`
5. Jalankan file:

   
---

## 📸 Catatan
- Semua icon disimpan di `src/main/resources`
- Folder `target/` tidak disertakan dalam repository
- Aplikasi dijalankan melalui JFrame (Java Swing)

---

## 👤 Author
**Kenny Naru**  
Mahasiswa S1 Sistem Informasi  
ITB STIKOM Bali  

---

## 📄 Lisensi
Project ini dibuat untuk keperluan pembelajaran dan tugas akademik.

