package com.example.financetracker.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:finance.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Membuat tabel transaksi
            String createTransaksiTable = """
                CREATE TABLE IF NOT EXISTS transaksi (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tanggal DATE NOT NULL,
                    kategori VARCHAR(50) NOT NULL,
                    deskripsi TEXT,
                    jenis VARCHAR(10) NOT NULL CHECK(jenis IN ('PEMASUKAN', 'PENGELUARAN')),
                    jumlah DECIMAL(15,2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;

            // Membuat tabel kategori
            String createKategoriTable = """
                CREATE TABLE IF NOT EXISTS kategori (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama VARCHAR(50) UNIQUE NOT NULL,
                    jenis VARCHAR(10) NOT NULL CHECK(jenis IN ('PEMASUKAN', 'PENGELUARAN'))
                )
            """;

            Statement stmt = conn.createStatement();
            stmt.execute(createTransaksiTable);
            stmt.execute(createKategoriTable);

            insertDefaultKategori(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDefaultKategori(Connection conn) throws SQLException {
        String[] pemasukanKategori = {"Gaji", "Bonus", "Investasi", "Lain-lain"};
        String[] pengeluaranKategori = {"Makanan", "Transport", "Belanja", "Tagihan", "Hiburan"};

        String insertKategori = "INSERT OR IGNORE INTO kategori(nama, jenis) VALUES(?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertKategori);

        for (String kategori : pemasukanKategori) {
            pstmt.setString(1, kategori);
            pstmt.setString(2, "PEMASUKAN");
            pstmt.executeUpdate();
        }

        for (String kategori : pengeluaranKategori) {
            pstmt.setString(1, kategori);
            pstmt.setString(2, "PENGELUARAN");
            pstmt.executeUpdate();
        }
    }
}
