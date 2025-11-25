package com.example.smartspend;

import android.content.Context;
import android.util.Log;

import com.example.smartspend.model.Transaksi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    private static final String FILE_NAME_TRANSAKSI = "transaksi.json";
    private static final String FILE_NAME_SALDO = "saldo.json";
    private static final String TAG = "FileHelper";

    // --- FUNGSI TRANSAKSI (Tetap seperti sebelumnya) ---
    public static void simpanTransaksi(Context context, Transaksi transaksi) {
        List<Transaksi> list = ambilSemuaTransaksi(context);
        list.add(transaksi);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        try (FileOutputStream fos = context.openFileOutput(FILE_NAME_TRANSAKSI, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
            Log.d(TAG, "Transaksi disimpan ke file: " + transaksi.jumlah);
        } catch (IOException e) {
            Log.e(TAG, "Gagal simpan transaksi ke file: " + e.getMessage());
        }
    }

    public static List<Transaksi> ambilSemuaTransaksi(Context context) {
        List<Transaksi> list = new ArrayList<>();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Transaksi>>(){}.getType();

        try (FileInputStream fis = context.openFileInput(FILE_NAME_TRANSAKSI);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            if (!json.isEmpty()) {
                list = gson.fromJson(json, listType);
            }
        } catch (IOException e) {
            Log.e(TAG, "Gagal baca transaksi dari file: " + e.getMessage());
        }
        return list;
    }

    // --- FUNGSI SALDO (Baru) ---
    public static void simpanSaldo(Context context, long jumlah) {
        Saldo saldo = new Saldo(jumlah);
        Gson gson = new Gson();
        String json = gson.toJson(saldo);

        try (FileOutputStream fos = context.openFileOutput(FILE_NAME_SALDO, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
            Log.d(TAG, "Saldo disimpan ke file: " + jumlah);
        } catch (IOException e) {
            Log.e(TAG, "Gagal simpan saldo ke file: " + e.getMessage());
        }
    }

    public static long ambilSaldo(Context context) {
        long saldo = 0;
        Gson gson = new Gson();

        try (FileInputStream fis = context.openFileInput(FILE_NAME_SALDO);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            if (!json.isEmpty()) {
                Saldo s = gson.fromJson(json, Saldo.class);
                if (s != null) {
                    saldo = s.jumlah;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Gagal baca saldo dari file: " + e.getMessage());
            // Jika file belum ada, kembalikan 0
        }
        return saldo;
    }

    // Fungsi untuk menambah saldo
    public static void tambahSaldo(Context context, long jumlah) {
        long saldoSekarang = ambilSaldo(context);
        long saldoBaru = saldoSekarang + jumlah;
        simpanSaldo(context, saldoBaru);
    }

    // Fungsi untuk mengurangi saldo
    public static void kurangiSaldo(Context context, long jumlah) {
        long saldoSekarang = ambilSaldo(context);
        long saldoBaru = saldoSekarang - jumlah;
        simpanSaldo(context, Math.max(0, saldoBaru)); // Jangan sampai minus
    }
}