package com.example.myapplication;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import java.util.regex.Pattern;
import com.example.myapplication.TransactionManager;
import java.util.regex.Matcher;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "NOTIF_DEBUG";
    private static final String[] BANK_PACKAGES = {
            "com.bank.mandiri",
            "com.bca.mobile",
            "com.bni.mobile",
            "com.bri.mobile",
            "com.mand.notitest",      // ✅ untuk testing
            "id.co.dbs.star"
    };

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "✅ NotificationListener aktif dan siap menerima notifikasi!");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pkg = sbn.getPackageName();
        String title = sbn.getNotification().extras.getString("android.title", "");
        String text = sbn.getNotification().extras.getString("android.text", "");
        String fullText = (title + " " + text).trim();

        // 🔍 Log detail notifikasi masuk
        Log.d(TAG, "──────────────────────────────");
        Log.d(TAG, "📦 Package : " + pkg);
        Log.d(TAG, "📌 Judul   : " + title);
        Log.d(TAG, "📝 Isi     : " + text);
        Log.d(TAG, "📄 Gabungan: " + fullText);
        Log.d(TAG, "──────────────────────────────");

        // Cek apakah dari bank yang didukung
        boolean isSupported = false;
        for (String bank : BANK_PACKAGES) {
            if (pkg.equals(bank)) {
                isSupported = true;
                break;
            }
        }

        if (!isSupported) {
            Log.d(TAG, "ℹ️ Dilewatkan: bukan bank yang didukung");
            return;
        }

        Log.d(TAG, "✅ Paket didukung — lanjut ekstraksi nominal");
        long amount = extractAmount(fullText);
        if (amount > 0) {
            Log.d(TAG, "💰 Nominal terdeteksi: Rp" + amount);
            TransactionManager.recordTransaction(this, amount);
            Log.d(TAG, "💾 Disimpan ke realisasi hari ini");
        } else {
            Log.w(TAG, "❌ Gagal ekstrak nominal dari teks notifikasi");
        }
    }

    /**
     * Ekstrak angka dari teks notifikasi.
     * Contoh input:
     *   "Transaksi keluar Rp50.000" → 50000
     *   "Bayar QRIS 75000 sukses" → 75000
     *   "Pembayaran Es Teh Rp. 3000 telah berhasil" → 3000
     */
    private long extractAmount(String fullText) {
        if (fullText == null || fullText.isEmpty()) return 0;

        String lower = fullText.toLowerCase();

        // Abaikan notifikasi pemasukan atau saldo
        if (lower.contains("masuk") || lower.contains("saldo") || lower.contains("top up")) {
            Log.d(TAG, "🚫 Diabaikan: notifikasi pemasukan atau saldo");
            return 0;
        }

        // Regex untuk ekstrak angka setelah "Rp" (opsional) atau angka utuh ≥1000
        // Contoh: Rp50.000, Rp. 3000, 75000, bayar 25000
        Pattern pattern = Pattern.compile("(?i)(?:Rp\\.?\\s*)?(\\d{1,3}(?:\\.\\d{3})+|\\d{4,})");
        Matcher matcher = pattern.matcher(fullText);

        while (matcher.find()) {
            String numberStr = matcher.group(1).replace(".", ""); // hapus titik ribuan
            try {
                long value = Long.parseLong(numberStr);
                // Filter nilai wajar: Rp1.000 – Rp100.000.000
                if (value >= 1000 && value <= 100_000_000) {
                    // Opsional: tambahkan logika arah transaksi lebih ketat
                    return value;
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Gagal parse angka: " + numberStr, e);
            }
        }

        Log.d(TAG, "🔍 Tidak ditemukan angka valid dalam: " + fullText);
        return 0;
    }
}