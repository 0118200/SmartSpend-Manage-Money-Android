package com.example.smartspend.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartspend.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAMA_DATABASE = "smartspend.db";
    private static final int VERSI_DATABASE = 2; // Naikkan versi karena ada perubahan struktur

    private static final String TABEL_TRANSAKSI = "transaksi";
    private static final String KOLOM_ID = "id";
    private static final String KOLOM_JUDUL = "judul";
    private static final String KOLOM_DESKRIPSI = "deskripsi";
    private static final String KOLOM_JUMLAH = "jumlah";
    private static final String KOLOM_WAKTU = "waktu";

    // Tabel baru untuk menyimpan saldo
    private static final String TABEL_SALDO = "saldo";
    private static final String KOLOM_SALDO_ID = "id";
    private static final String KOLOM_SALDO_JUMLAH = "jumlah";

    public DatabaseHelper(Context konteks) {
        super(konteks, NAMA_DATABASE, null, VERSI_DATABASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Buat tabel transaksi
        String BUAT_TABEL_TRANSAKSI = "CREATE TABLE " + TABEL_TRANSAKSI + "("
                + KOLOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KOLOM_JUDUL + " TEXT,"
                + KOLOM_DESKRIPSI + " TEXT,"
                + KOLOM_JUMLAH + " INTEGER,"
                + KOLOM_WAKTU + " INTEGER" + ")";
        db.execSQL(BUAT_TABEL_TRANSAKSI);

        // Buat tabel saldo
        String BUAT_TABEL_SALDO = "CREATE TABLE " + TABEL_SALDO + "("
                + KOLOM_SALDO_ID + " INTEGER PRIMARY KEY,"
                + KOLOM_SALDO_JUMLAH + " INTEGER DEFAULT 0" + ")";
        db.execSQL(BUAT_TABEL_SALDO);

        // Set default saldo = 0
        ContentValues nilai = new ContentValues();
        nilai.put(KOLOM_SALDO_ID, 1); // Hanya satu baris saldo
        nilai.put(KOLOM_SALDO_JUMLAH, 0);
        db.insert(TABEL_SALDO, null, nilai);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versiLama, int versiBaru) {
        // Hapus tabel lama jika ada (termasuk data)
        db.execSQL("DROP TABLE IF EXISTS " + TABEL_TRANSAKSI);
        db.execSQL("DROP TABLE IF EXISTS " + TABEL_SALDO);
        onCreate(db);
    }

    // Fungsi untuk mengatur saldo
    public void setSaldo(long jumlah) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues nilai = new ContentValues();
        nilai.put(KOLOM_SALDO_JUMLAH, jumlah);
        db.update(TABEL_SALDO, nilai, KOLOM_SALDO_ID + " = ?", new String[]{"1"});
        db.close();
    }

    // Fungsi untuk mendapatkan saldo saat ini
    public long getSaldo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KOLOM_SALDO_JUMLAH + " FROM " + TABEL_SALDO + " WHERE " + KOLOM_SALDO_ID + " = ?", new String[]{"1"});
        long saldo = 0;
        if (cursor.moveToFirst()) {
            saldo = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return saldo;
    }

    // Fungsi untuk menambah saldo
    public void tambahSaldo(long jumlah) {
        long saldoSekarang = getSaldo();
        setSaldo(saldoSekarang + jumlah);
    }

    // Fungsi untuk mengurangi saldo
    public void kurangiSaldo(long jumlah) {
        long saldoSekarang = getSaldo();
        setSaldo(saldoSekarang - jumlah);
    }

    public void simpanTransaksi(String judul, String deskripsi, long jumlah) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues nilai = new ContentValues();
        nilai.put(KOLOM_JUDUL, judul);
        nilai.put(KOLOM_DESKRIPSI, deskripsi);
        nilai.put(KOLOM_JUMLAH, jumlah);
        nilai.put(KOLOM_WAKTU, System.currentTimeMillis());
        db.insert(TABEL_TRANSAKSI, null, nilai);
        db.close();

        // Kurangi saldo setelah transaksi
        kurangiSaldo(jumlah);
    }

    public List<Transaksi> ambilSemuaTransaksi() {
        List<Transaksi> daftar = new ArrayList<>();
        String query = "SELECT * FROM " + TABEL_TRANSAKSI + " ORDER BY " + KOLOM_WAKTU + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int idxJudul = cursor.getColumnIndex(KOLOM_JUDUL);
            int idxDeskripsi = cursor.getColumnIndex(KOLOM_DESKRIPSI);
            int idxJumlah = cursor.getColumnIndex(KOLOM_JUMLAH);

            do {
                String judul = idxJudul >= 0 ? cursor.getString(idxJudul) : "";
                String deskripsi = idxDeskripsi >= 0 ? cursor.getString(idxDeskripsi) : "";
                long jumlah = idxJumlah >= 0 ? cursor.getLong(idxJumlah) : 0;

                Transaksi transaksi = new Transaksi(judul, deskripsi, jumlah);
                daftar.add(transaksi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return daftar;
    }
}