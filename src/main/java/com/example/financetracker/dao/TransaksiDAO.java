/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.financetracker.dao;

import com.example.financetracker.database.DatabaseManager;
import com.example.financetracker.model.Transaksi;
import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {
    
    // Method untuk mendapatkan transaksi berdasarkan ID
    public Transaksi getTransaksiById(int id) {
        String sql = "SELECT * FROM transaksi WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(rs.getInt("id"));
                transaksi.setTanggal(rs.getDate("tanggal").toLocalDate());
                transaksi.setKategori(rs.getString("kategori"));
                transaksi.setDeskripsi(rs.getString("deskripsi"));
                transaksi.setJenis(Transaksi.JenisTransaksi.valueOf(rs.getString("jenis")));
                transaksi.setJumlah(rs.getBigDecimal("jumlah"));
                
                return transaksi;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Method untuk mengupdate transaksi
    public boolean updateTransaksi(Transaksi transaksi) {
        String sql = "UPDATE transaksi SET tanggal = ?, kategori = ?, deskripsi = ?, jenis = ?, jumlah = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(transaksi.getTanggal()));
            pstmt.setString(2, transaksi.getKategori());
            pstmt.setString(3, transaksi.getDeskripsi());
            pstmt.setString(4, transaksi.getJenis().toString());
            pstmt.setBigDecimal(5, transaksi.getJumlah());
            pstmt.setInt(6, transaksi.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method untuk menghapus transaksi
    public boolean hapusTransaksi(int id) {
        String sql = "DELETE FROM transaksi WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private DatabaseManager dbManager;

    public TransaksiDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean tambahTransaksi(Transaksi transaksi) {
        String sql = "INSERT INTO transaksi(tanggal, kategori, deskripsi, jenis, jumlah) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(transaksi.getTanggal()));
            pstmt.setString(2, transaksi.getKategori());
            pstmt.setString(3, transaksi.getDeskripsi());
            pstmt.setString(4, transaksi.getJenis().toString());
            pstmt.setBigDecimal(5, transaksi.getJumlah());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaksi> getAllTransaksi() {
        List<Transaksi> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(rs.getInt("id"));
                transaksi.setTanggal(rs.getDate("tanggal").toLocalDate());
                transaksi.setKategori(rs.getString("kategori"));
                transaksi.setDeskripsi(rs.getString("deskripsi"));
                transaksi.setJenis(Transaksi.JenisTransaksi.valueOf(rs.getString("jenis")));
                transaksi.setJumlah(rs.getBigDecimal("jumlah"));
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaksiList;
    }

    public BigDecimal getTotalPemasukan() {
        String sql = "SELECT SUM(jumlah) FROM transaksi WHERE jenis='PEMASUKAN'";
        return getTotal(sql);
    }

    public BigDecimal getTotalPengeluaran() {
        String sql = "SELECT SUM(jumlah) FROM transaksi WHERE jenis='PENGELUARAN'";
        return getTotal(sql);
    }

    private BigDecimal getTotal(String sql) {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}