/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.financetracker.dao;

import com.example.financetracker.database.DatabaseManager;
import com.example.financetracker.model.Transaksi;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import java.io.FileNotFoundException;

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
    
    public void exportToExcel(String filePath, List<Transaksi> transaksiList) {
        Workbook workbook = new XSSFWorkbook(); // Untuk .xlsx
        Sheet sheet = workbook.createSheet("Transaksi");

        // Header kolom
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Tanggal");
        headerRow.createCell(2).setCellValue("Kategori");
        headerRow.createCell(3).setCellValue("Deskripsi");
        headerRow.createCell(4).setCellValue("Jenis");
        headerRow.createCell(5).setCellValue("Jumlah");

        // Isi data
        int rowNum = 1;
        for (Transaksi transaksi : transaksiList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaksi.getId());
            row.createCell(1).setCellValue(transaksi.getTanggal().toString());
            row.createCell(2).setCellValue(transaksi.getKategori());
            row.createCell(3).setCellValue(transaksi.getDeskripsi());
            row.createCell(4).setCellValue(transaksi.getJenis().toString());
            row.createCell(5).setCellValue(transaksi.getJumlah().toString());
        }

        // Autofit kolom
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        // Simpan ke file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void exportToPDF(String filePath, List<Transaksi> transaksiList) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Judul dokumen
            document.add(new Paragraph("Laporan Transaksi").setFontSize(16).setBold());

            // Tabel
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 3, 4, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Header kolom
            table.addCell("ID");
            table.addCell("Tanggal");
            table.addCell("Kategori");
            table.addCell("Deskripsi");
            table.addCell("Jenis");
            table.addCell("Jumlah");

            // Isi data
            for (Transaksi transaksi : transaksiList) {
                table.addCell(String.valueOf(transaksi.getId()));
                table.addCell(transaksi.getTanggal().toString());
                table.addCell(transaksi.getKategori());
                table.addCell(transaksi.getDeskripsi());
                table.addCell(transaksi.getJenis().toString());
                table.addCell(transaksi.getJumlah().toString());
            }

            document.add(table);
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}