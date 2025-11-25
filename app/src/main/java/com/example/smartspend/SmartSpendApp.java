package com.example.smartspend;

import android.app.Application;
import android.util.Log;

import com.example.smartspend.db.DatabaseHelper;

public class SmartSpendApp extends Application {

    private static final String TAG = "SmartSpendApp";
    private static DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "✅ SmartSpendApp onCreate");

        // Inisialisasi DatabaseHelper di sini, di Application class
        // Agar bisa digunakan di mana saja tanpa crash
        try {
            databaseHelper = new DatabaseHelper(this);
            Log.d(TAG, "✅ DatabaseHelper berhasil diinisialisasi di Application");
        } catch (Exception e) {
            Log.e(TAG, "❌ Gagal inisialisasi DatabaseHelper: " + e.getMessage(), e);
        }
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}