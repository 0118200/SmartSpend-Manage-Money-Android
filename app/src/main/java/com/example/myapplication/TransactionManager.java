// app/src/main/java/com/example/myapplication/TransactionManager.java

package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.example.myapplication.utils.KategoriDetector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionManager {

    public static void recordTransactionWithCategory(Context context, long amount, String kategori) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String bulan = new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        // Simpan per hari
        String keyHarian = "harian_" + kategori + "_" + today;
        float totalHarianLama = prefs.getFloat(keyHarian, 0f);
        editor.putFloat(keyHarian, totalHarianLama + amount);

        // Simpan per bulan
        String keyBulanan = "bulanan_" + kategori + "_" + bulan;
        float totalBulananLama = prefs.getFloat(keyBulanan, 0f);
        editor.putFloat(keyBulanan, totalBulananLama + amount);

        editor.apply();
        // ❌ TIDAK ADA FileHelper.kurangiSaldo()
    }

    // app/src/main/java/com/example/myapplication/TransactionManager.java

    public static float getRealizationByDate(Context context, String tanggal) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        float total = 0;
        String[] kategoriList = {"Makanan", "Transportasi", "Belanja", "Hiburan", "PulsaData", "Lainnya"};
        for (String k : kategoriList) {
            total += prefs.getFloat("harian_" + k + "_" + tanggal, 0f);
        }
        return total;
    }

    public static float getTotalTodayRealization(Context context) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        float total = 0;
        String[] kategoriList = {"Makanan", "Transportasi", "Belanja", "Hiburan", "PulsaData", "Lainnya"};
        for (String k : kategoriList) {
            total += prefs.getFloat("harian_" + k + "_" + today, 0f);
        }
        return total;
    }

    public static float getTodayByCategory(Context context, String kategori) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat("harian_" + kategori + "_" + today, 0f);
    }
}