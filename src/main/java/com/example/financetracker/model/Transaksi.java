/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.one.financetracker.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Transaksi {
    private int id;
    private LocalDateTime tanggal;
    private String kategori;
    private String deskripsi;
    private JenisTransaksi jenis;
    private BigDecimal jumlah;

    public enum JenisTransaksi { PEMASUKAN, PENGELUARAN }

    public Transaksi() {}

    public Transaksi(LocalDateTime tanggal, String kategori, String deskripsi, JenisTransaksi jenis, BigDecimal jumlah) {
        this.id = id;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.jenis = jenis;
        this.jumlah = jumlah;
    }

    // Getter dan Setter
    public int getId() {return id; }
    public void setId(int id) {this.id = id; }

    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public JenisTransaksi getJenis() { return jenis; }
    public void setJenis(JenisTransaksi jenis) { this.jenis = jenis; }

    public BigDecimal getJumlah() { return jumlah; }
    public void setJumlah(BigDecimal jumlah) { this.jumlah = jumlah; }
}