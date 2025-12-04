// File: app/src/main/java/com/example/myapplication/model/Transaksi.java
package com.example.myapplication.model;

public class Transaksi {
    public String judul;
    public String isi;
    public long jumlah;

    public Transaksi(String judul, String isi, long jumlah) {
        this.judul = judul;
        this.isi = isi;
        this.jumlah = jumlah;
    }
}