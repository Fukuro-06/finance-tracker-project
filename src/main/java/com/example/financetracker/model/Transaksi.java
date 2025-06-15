package com.example.financetracker.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Transaksi {
    private int id;
    private LocalDate tanggal;
    private String kategori;
    private String deskripsi;
    private JenisTransaksi jenis;
    private BigDecimal jumlah;

    public enum JenisTransaksi { PEMASUKAN, PENGELUARAN }

    public Transaksi() {}

    public Transaksi(LocalDate tanggal, String kategori, String deskripsi, JenisTransaksi jenis, BigDecimal jumlah) {
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.jenis = jenis;
        this.jumlah = jumlah;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public JenisTransaksi getJenis() { return jenis; }
    public void setJenis(JenisTransaksi jenis) { this.jenis = jenis; }

    public BigDecimal getJumlah() { return jumlah; }
    public void setJumlah(BigDecimal jumlah) { this.jumlah = jumlah; }
}
