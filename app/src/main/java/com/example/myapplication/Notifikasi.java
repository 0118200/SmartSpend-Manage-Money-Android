// app/src/main/java/com/example/myapplication/Notifikasi.java

package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import com.example.myapplication.model.Transaksi;
import com.example.myapplication.utils.KategoriDetector;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Notifikasi extends NotificationListenerService {

    private static final String TAG = "NOTIF_DEBUG";

    private static final String[] SUPPORTED_PACKAGES = {
            "com.bca.mobile",
            "com.bni.mobile",
            "com.bri.mobile",
            "com.bank.mandiri",
            "com.mand.notitest",
            "com.dana",
            "com.ovo.id",
            "com.gojek.app",
            "id.co.dbs.star"
    };

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pkg = sbn.getPackageName();
        Log.d(TAG, "──────────────────────────────");
        Log.d(TAG, "📬 Notifikasi DITERIMA dari: " + pkg);

        boolean isSupported = false;
        for (String supported : SUPPORTED_PACKAGES) {
            if (pkg.equals(supported)) {
                isSupported = true;
                break;
            }
        }
        if (!isSupported) {
            Log.d(TAG, "ℹ️ Dilewatkan: bukan bank yang didukung");
            Log.d(TAG, "──────────────────────────────");
            return;
        }

        // Ekstrak teks lengkap
        android.app.Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        StringBuilder fullText = new StringBuilder();
        appendIfNotNull(fullText, extras.getCharSequence("android.title"));
        appendIfNotNull(fullText, extras.getCharSequence("android.text"));
        appendIfNotNull(fullText, extras.getCharSequence("android.bigText"));
        appendIfNotNull(fullText, extras.getCharSequence("android.summaryText"));

        CharSequence[] lines = extras.getCharSequenceArray("android.textLines");
        if (lines != null) {
            for (CharSequence line : lines) {
                appendIfNotNull(fullText, line);
            }
        }
        if (notification.tickerText != null) {
            fullText.append(notification.tickerText);
        }

        String textStr = fullText.toString().trim();
        Log.d(TAG, "✨ Teks gabungan: [" + textStr + "]");

        if (textStr.isEmpty()) {
            Log.w(TAG, "❌ Teks kosong — tidak bisa ekstrak nominal");
            Log.d(TAG, "──────────────────────────────");
            return;
        }

        // Abaikan pemasukan
        if (isPemasukan(textStr)) {
            Log.d(TAG, "🚫 Diabaikan: notifikasi pemasukan/saldo");
            Log.d(TAG, "──────────────────────────────");
            return;
        }

        long amount = extractAmount(textStr);
        if (amount <= 0) {
            Log.w(TAG, "❌ Gagal ekstrak nominal");
            Log.d(TAG, "──────────────────────────────");
            return;
        }

        String title = extras.getCharSequence("android.title") != null ?
                extras.getCharSequence("android.title").toString() : "Transaksi Bank";
        String deskripsi = textStr; // 👈 ini yang dipakai untuk deteksi kategori

        // ✅ 1. Simpan ke riwayat global (FileHelper)
        Transaksi transaksi = new Transaksi(title, deskripsi, amount);
        FileHelper.simpanTransaksi(this, transaksi);
        String kategori = KategoriDetector.deteksiKategori(deskripsi);

        if (kategori == null || kategori.trim().isEmpty()) {
            kategori = "Lainnya"; // fallback
        }

// Format timestamp
        // Format timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US).format(new Date());
        String key = "transaksi_" + timestamp;

// Simpan ke SharedPreferences (untuk history)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putString(key + "_judul", title)
                .putString(key + "_deskripsi", deskripsi)
                .putLong(key + "_jumlah", amount)
                .putString(key + "_kategori", kategori)
                .apply();

        Log.d("HISTORY_SAVE", "Notifikasi disimpan: " + key);
        // ✅ 2. Simpan ke kategori HARI INI (TransactionManager) — ini yang kamu butuhkan untuk UI
        TransactionManager.recordTransactionWithCategory(this, amount, kategori); // ✅ 3 parameter!

        // ✅ 3. Logika budget harian & dana darurat
        // Hitung apakah over budget
        float budgetHarian = calculateDailyBudget();
        float terpakaiSebelum = TransactionManager.getTotalTodayRealization(this);
        float terpakaiBaru = terpakaiSebelum + amount;

        if (terpakaiBaru > budgetHarian) {
            float overBudget = terpakaiBaru - budgetHarian;
            float danaDaruratSisa = getDanaDaruratSisa();

            if (danaDaruratSisa >= overBudget) {
                // Ambil dari dana darurat
                setDanaDaruratSisa(danaDaruratSisa - overBudget);
                Log.d(TAG, "🛡️ Dana darurat berkurang: Rp" + overBudget);
            } else {
                // Dana darurat tidak cukup → potong saldo global
                float kurang = overBudget - danaDaruratSisa;
                setDanaDaruratSisa(0);
                FileHelper.kurangiSaldo(this, (long) kurang);
                Log.d(TAG, "⚠️ Potong saldo global: Rp" + kurang);
            }
        }
// Jika TIDAK over budget, JANGAN KURANGI SALDO GLOBAL!
// Karena uang sudah dialokasikan di budget harian

        // ✅ 4. Log akhir
        float saldoGlobal = FileHelper.ambilSaldo(this);
        float danaSisa = getDanaDaruratSisa();
        Log.d(TAG, "💰 Saldo global akhir: Rp" + saldoGlobal);
        Log.d(TAG, "🛡️ Dana darurat sisa: Rp" + danaSisa);
        Log.d(TAG, "✅ Sukses proses notifikasi dari: " + pkg);
        Log.d(TAG, "──────────────────────────────");
    }



    // === HELPER METHODS ===

    private boolean isPemasukan(String text) {
        String lower = text.toLowerCase();
        return lower.contains("masuk") || lower.contains("saldo") || lower.contains("top up") ||
                lower.contains("isi ulang") || lower.contains("berhasil ditambahkan") || lower.contains("dari");
    }

    private long extractAmount(String text) {
        if (text == null || text.isEmpty()) return 0;
        Pattern pattern = Pattern.compile("(?i)(?:Rp\\.?\\s*)?(\\d{1,3}(?:\\.\\d{3})+|\\d{4,})");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String numStr = matcher.group(1).replace(".", "").replace(",", "");
            try {
                long value = Long.parseLong(numStr);
                if (value >= 1000 && value <= 100_000_000) {
                    return value;
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Gagal parse angka: " + numStr);
            }
        }
        return 0;
    }

    private void appendIfNotNull(StringBuilder sb, CharSequence cs) {
        if (cs != null && !cs.toString().trim().isEmpty()) {
            sb.append(cs).append(" ");
        }
    }

    private float calculateDailyBudget() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float pemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);
        float saldoTersedia = Math.max(0, pemasukan - danaDarurat);

        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return saldoTersedia / daysInMonth;
    }

    private float getDanaDaruratSisa() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getFloat("dana_darurat_sisa", 0f);
    }

    private void setDanaDaruratSisa(float value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putFloat("dana_darurat_sisa", value).apply();
    }


}