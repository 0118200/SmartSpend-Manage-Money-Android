package com.example.smartspend.model;

public class Transaksi {
    public String judul;
    public String isi;
    public long jumlah;
    public long waktu;

    public Transaksi(String judul, String isi, long jumlah) {
        this.judul = judul;
        this.isi = isi;
        this.jumlah = jumlah;
        this.waktu = System.currentTimeMillis();
    }
}