package com.example.smartspend;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.content.Context; // Tambahkan ini
import android.os.Bundle;

import com.example.smartspend.model.Transaksi;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Notifikasi extends NotificationListenerService {

    private static final String TAG = "NotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        String packageName = sbn.getPackageName();
        Log.d(TAG, "Notifikasi dari: " + packageName);

        if (cekAplikasiBank(packageName)) {
            Bundle extras = sbn.getNotification().extras;

            CharSequence title = extras.getCharSequence("android.title");
            CharSequence text = extras.getCharSequence("android.text");
            CharSequence bigText = extras.getCharSequence("android.bigText");
            CharSequence summaryText = extras.getCharSequence("android.summaryText");

            String judul = title != null ? title.toString() : "(Tidak ada judul)";
            String isi = text != null ? text.toString() :
                    bigText != null ? bigText.toString() :
                            summaryText != null ? summaryText.toString() : "";

            Log.d(TAG, "Judul: " + judul);
            Log.d(TAG, "Isi: " + isi);

            // Coba parsing jumlah
            Pattern pattern = Pattern.compile("Rp\\.?([\\d.,]+)");
            Matcher matcher = pattern.matcher(isi);
            if (matcher.find()) {
                String jumlahStr = matcher.group(1);
                String jumlahBersih = jumlahStr.replaceAll("[.,]", "");
                try {
                    long jumlah = Long.parseLong(jumlahBersih);
                    Log.d(TAG, "Jumlah transaksi: " + jumlah);

                    // Simpan ke file
                    Transaksi transaksi = new Transaksi(judul, isi, jumlah);
                    FileHelper.simpanTransaksi(this, transaksi);

                    // 🔥 Kurangi saldo
                    FileHelper.kurangiSaldo(this, jumlah);
                    Log.d(TAG, "Saldo dikurangi sebesar: " + jumlah);

                } catch (NumberFormatException e) {
                    Log.e(TAG, "Gagal parsing jumlah: " + jumlahBersih);
                }
            }
        }
    }

    private boolean cekAplikasiBank(String packageName) {
        String[] bankApps = {
                "com.bca", "com.ovo.id", "com.gojek.app", "com.dana", "com.linkaja", "com.mand.notitest"
        };
        for (String app : bankApps) {
            if (packageName.contains(app)) return true;
        }
        return false;
    }
}