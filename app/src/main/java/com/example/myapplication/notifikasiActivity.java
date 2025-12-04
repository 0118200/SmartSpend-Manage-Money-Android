package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class notifikasiActivity extends AppCompatActivity {

    private LinearLayout containerNotifikasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifikasi);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        containerNotifikasi = findViewById(R.id.container_notifikasi);
        loadNotifications();
    }

    private void loadNotifications() {
        containerNotifikasi.removeAllViews();

        List<NotifItem> notifs = new ArrayList<>();

        // 1. Pengingat harian
        notifs.add(new NotifItem("NOTIFIKASI",
                "Hai! Jangan lupa catat pengeluaran kamu hari ini biar keuangan tetap terkontrol 💰✨"));

        // 2. Cek apakah ada pengeluaran hari ini
        float todaySpent = TransactionManager.getTotalTodayRealization(this);
        if (todaySpent == 0) {
            notifs.add(new NotifItem("NOTIFIKASI",
                    "Sudah beli apa aja hari ini? Yuk input pengeluaran sebelum lupa! 📝"));
        }

        // 3. Cek apakah mendekati batas bulanan
        float totalBulanan = getTotalBulanan();
        float pemasukan = PreferenceManager.getDefaultSharedPreferences(this)
                .getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = PreferenceManager.getDefaultSharedPreferences(this)
                .getFloat("dana_darurat_nominal", 0f);
        float saldoTersedia = pemasukan - danaDarurat;
        if (saldoTersedia > 0) {
            float persen = (totalBulanan / saldoTersedia) * 100;
            if (persen >= 80) {
                notifs.add(new NotifItem("NOTIFIKASI",
                        "Perhatian! Pengeluaran bulan ini sudah mencapai " +
                                String.format("%.0f", persen) + "% dari batas anggaran 😬. Saatnya lebih hemat!"));
            }
        }

        // 4. Cek kategori yang hampir over
        String[] kategoriList = {"Makanan", "Transportasi", "Belanja", "Hiburan"};
        float budgetPerKategori = saldoTersedia / kategoriList.length;
        for (String k : kategoriList) {
            float spent = getBulananKategori(k);
            if (budgetPerKategori > 0) {
                float persenKategori = (spent / budgetPerKategori) * 100;
                if (persenKategori >= 80) {
                    notifs.add(new NotifItem("NOTIFIKASI",
                            "Pengeluaran kategori " + k + " sudah hampir melewati batas. Coba tahan dulu, ya 🛍️💡"));
                }
            }
        }

        // 5. Laporan bulanan siap (jika sudah akhir bulan)
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        int hariIni = Integer.parseInt(sdf.format(new Date()));
        if (hariIni >= 28) {
            notifs.add(new NotifItem("NOTIFIKASI",
                    "Laporan keuangan bulan ini sudah siap! 🎉 Lihat rincian pengeluaran kamu dan temukan area yang bisa dihemat."));
            notifs.add(new NotifItem("NOTIFIKASI",
                    "Waktunya evaluasi keuangan bulan ini 📊 — buka laporanmu untuk tahu kemana uangmu pergi."));
        }

        // Tampilkan semua notifikasi
        for (NotifItem item : notifs) {
            addNotificationItem(item.title, item.message);
        }
    }

    private void addNotificationItem(String title, String message) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackgroundResource(R.drawable.bg_notif_item);
        itemLayout.setPadding(15, 15, 15, 15);
        itemLayout.setBottom(10);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(getResources().getColor(R.color.notif_border_gold));
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextSize(14);
        tvTitle.setBottom(5);

        TextView tvMessage = new TextView(this);
        tvMessage.setText(message);
        tvMessage.setTextColor(getResources().getColor(R.color.black));
        tvMessage.setTextSize(13);

        itemLayout.addView(tvTitle);
        itemLayout.addView(tvMessage);
        containerNotifikasi.addView(itemLayout);

        // Tambahkan margin bottom
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 10;
        itemLayout.setLayoutParams(params);
    }

    private float getTotalBulanan() {
        String bulanIni = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float total = 0;
        String[] kategoriList = {"Makanan", "Transportasi", "Belanja", "Hiburan", "PulsaData", "Lainnya"};
        for (String k : kategoriList) {
            total += prefs.getFloat("bulanan_" + k + "_" + bulanIni, 0f);
        }
        return total;
    }

    private float getBulananKategori(String kategori) {
        String bulanIni = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getFloat("bulanan_" + kategori + "_" + bulanIni, 0f);
    }

    // Inner class untuk notifikasi
    private static class NotifItem {
        String title;
        String message;

        NotifItem(String title, String message) {
            this.title = title;
            this.message = message;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kembali ke homepage
        finish();
    }
}