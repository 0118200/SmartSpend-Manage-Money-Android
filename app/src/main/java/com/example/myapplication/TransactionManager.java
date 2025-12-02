package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Calendar;

public class TransactionManager {

    public static void recordTransaction(Context context, long amount) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        // Ambil hari ini (1–31)
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Baca realisasi hari ini
        String key = "realisasi_hari_" + day;
        float current = prefs.getFloat(key, 0f);

        // Tambahkan pengeluaran baru
        float updated = current + amount;

        // Simpan
        editor.putFloat(key, updated);
        editor.apply();

        // Optional: log
        android.util.Log.d("TransactionManager", "Hari " + day + ": Realisasi = " + updated);
    }

    // Helper: baca realisasi hari ini
    public static float getRealizationByDay(Context context, int day) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat("realisasi_hari_" + day, 0f);
    }

    public static float getTodayRealization(Context context) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat("realisasi_hari_" + day, 0f);
    }
}