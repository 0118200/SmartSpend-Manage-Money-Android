// File: app/src/main/java/com/example/myapplication/FileHelper.java
package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.example.myapplication.model.Transaksi;
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
        }
        return saldo;
    }

    public static void tambahSaldo(Context context, long jumlah) {
        long saldoSekarang = ambilSaldo(context);
        long saldoBaru = saldoSekarang + jumlah;
        simpanSaldo(context, saldoBaru);
    }

    // Di FileHelper.java
    public static void kurangiSaldo(Context context, float jumlah) {
        long saldoSekarang = ambilSaldo(context);
        long saldoBaru = (long) (saldoSekarang - jumlah);
        simpanSaldo(context, Math.max(0, saldoBaru));
    }

    // app/src/main/java/com/example/myapplication/FileHelper.java

    public static void kurangiSaldo(Context context, long amount) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        float saldo = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);
        float saldoTersedia = saldo - danaDarurat;

        // Jika saldo tersedia > 0, kurangi
        if (saldoTersedia > 0) {
            float newSaldo = saldo - amount;
            prefs.edit().putFloat("jumlah_pemasukan", newSaldo).apply();
        } else {
            // Jika sudah habis, jangan kurangi lagi
            Toast.makeText(context, "Saldo habis!", Toast.LENGTH_SHORT).show();
        }
    }

    // Di FileHelper.java

    public static void initSaldoBulanan(Context context, float pemasukan) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Hanya set jika belum pernah di-set
        if (!prefs.contains("saldo_bulanan_tersedia")) {
            prefs.edit().putFloat("saldo_bulanan_tersedia", pemasukan).apply();
        }
    }

    public static void kurangiSaldoBulanan(Context context, float jumlah) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        float saldo = prefs.getFloat("saldo_bulanan_tersedia", 0f);
        float newSaldo = Math.max(0, saldo - jumlah);
        prefs.edit().putFloat("saldo_bulanan_tersedia", newSaldo).apply();
    }

    public static float ambilSaldoBulananTersedia(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat("saldo_bulanan_tersedia", 0f);
    }
}