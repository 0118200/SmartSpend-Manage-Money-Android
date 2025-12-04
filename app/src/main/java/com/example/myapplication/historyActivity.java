package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.widget.ArrayAdapter;
import java.util.Set;

public class historyActivity extends AppCompatActivity {

    private Spinner spinnerBulan, spinnerTahun;
    private TextView tvTotalPengeluaran, tvRincianButton;
    private LinearLayout containerRiwayat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView btnRincian = findViewById(R.id.btn_rincian);
        btnRincian.setOnClickListener(v -> {
            Toast.makeText(this, "Rincian diklik!", Toast.LENGTH_SHORT).show();
        });

        // Inisialisasi view
        spinnerBulan = findViewById(R.id.spinner_bulan);
        spinnerTahun = findViewById(R.id.spinner_tahun);
        tvTotalPengeluaran = findViewById(R.id.tv_total_pengeluaran);
        tvRincianButton = findViewById(R.id.btn_rincian);
        containerRiwayat = findViewById(R.id.container_riwayat);

        // 💡 Tambahkan DI SINI: Set pilihan awal ke bulan & tahun saat ini
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH); // Desember = 11
        int currentYear = now.get(Calendar.YEAR);

        spinnerBulan.setSelection(currentMonth);

        // Cari posisi tahun di spinner
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerTahun.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(String.valueOf(currentYear))) {
                spinnerTahun.setSelection(i);
                break;
            }
        }

        // Set listener
        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerBulan.getSelectedItem().toString();
                Log.d("SPINNER", "Bulan dipilih: " + selected);
                loadHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerTahun.getSelectedItem().toString();
                Log.d("SPINNER", "Tahun dipilih: " + selected);
                loadHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadHistory();
    }

    private void loadHistory() {
        String selectedBulanNama = spinnerBulan.getSelectedItem().toString();
        String selectedTahun = spinnerTahun.getSelectedItem().toString();

        if ("Bulan".equals(selectedBulanNama) || "Tahun".equals(selectedTahun)) {
            // Tampilkan pesan atau gunakan bulan/tahun saat ini
            Calendar now = Calendar.getInstance();
            selectedTahun = String.valueOf(now.get(Calendar.YEAR));
            selectedBulanNama = new SimpleDateFormat("MMMM", Locale.getDefault()).format(now.getTime());
        }

        String bulanAngka = getMonthNumber(selectedBulanNama);
        String periode = selectedTahun + "-" + bulanAngka;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        List<Transaksi> allTransactions = getAllTransactionsForPeriod(prefs, periode);

        float total = 0f;
        for (Transaksi t : allTransactions) {
            total += t.jumlah;
        }
        tvTotalPengeluaran.setText("Total Pengeluaran : Rp. " + formatCurrency(total));

        displayHistoryByDate(allTransactions);
    }

    private List<Transaksi> getAllTransactionsForPeriod(SharedPreferences prefs, String periode) {
        List<Transaksi> transactions = new ArrayList<>();
        Map<String, ?> allKeys = prefs.getAll();
        Log.d("HISTORY_DEBUG", "Mencari transaksi untuk periode: " + periode);

        // Kumpulkan semua timestamp unik
        Set<String> timestamps = new HashSet<>();
        for (String key : allKeys.keySet()) {
            if (key.startsWith("transaksi_") && key.contains(periode)) {
                // Ekstrak timestamp: "2025-12-04_11:38:37"
                int startIndex = "transaksi_".length();
                int endIndex = key.indexOf('_', startIndex);
                if (endIndex != -1) {
                    int secondEndIndex = key.indexOf('_', endIndex + 1);
                    if (secondEndIndex != -1) {
                        String timestamp = key.substring(startIndex, secondEndIndex);
                        timestamps.add(timestamp);
                    }
                }
            }
        }

        // Proses setiap timestamp sekali
        for (String timestamp : timestamps) {
            String baseKey = "transaksi_" + timestamp;
            String judul = prefs.getString(baseKey + "_judul", "Transaksi");
            String deskripsi = prefs.getString(baseKey + "_deskripsi", "");
            long jumlah = prefs.getLong(baseKey + "_jumlah", 0L);
            String kategori = prefs.getString(baseKey + "_kategori", "Lainnya");

            Transaksi t = new Transaksi(judul, deskripsi, jumlah);
            t.tanggal = timestamp.split("_")[0];
            t.kategori = kategori;
            t.waktu = timestamp.split("_")[1];
            transactions.add(t);
        }

        Collections.sort(transactions, new Comparator<Transaksi>() {
            @Override
            public int compare(Transaksi t1, Transaksi t2) {
                return t2.tanggal.compareTo(t1.tanggal);
            }
        });

        return transactions;
    }

    private void displayHistoryByDate(List<Transaksi> transactions) {
        containerRiwayat.removeAllViews();

        if (transactions.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Belum ada pengeluaran di bulan ini.");
            empty.setTextSize(16);
            empty.setPadding(20, 20, 20, 20);
            empty.setTextColor(getResources().getColor(android.R.color.darker_gray));
            containerRiwayat.addView(empty);
            return;
        }

        Map<String, List<Transaksi>> groupedByDate = new HashMap<>();
        for (Transaksi t : transactions) {
            groupedByDate.computeIfAbsent(t.tanggal, k -> new ArrayList<>()).add(t);
        }

        for (String tanggal : groupedByDate.keySet()) {
            List<Transaksi> dailyList = groupedByDate.get(tanggal);

            LinearLayout headerLayout = new LinearLayout(this);
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            headerLayout.setPadding(0, 10, 0, 5);

            ImageView iconCalendar = new ImageView(this);
            iconCalendar.setImageResource(R.drawable.ic_calendar_item);
            iconCalendar.setLayoutParams(new LinearLayout.LayoutParams(24, 24));
            iconCalendar.setPadding(0, 0, 10, 0);

            TextView tvTanggal = new TextView(this);
            tvTanggal.setText(tanggal);
            tvTanggal.setTextSize(14);
            tvTanggal.setTypeface(null, Typeface.BOLD);
            tvTanggal.setTextColor(getResources().getColor(R.color.black));

            headerLayout.addView(iconCalendar);
            headerLayout.addView(tvTanggal);
            containerRiwayat.addView(headerLayout);

            for (Transaksi t : dailyList) {
                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setPadding(34, 5, 10, 5);

                int iconRes = getCategoryIcon(t.kategori);
                int bgColor = getCategoryColor(t.kategori);

                CardView cardIcon = new CardView(this);
                cardIcon.setRadius(12f);
                cardIcon.setCardBackgroundColor(bgColor);
                cardIcon.setCardElevation(0f);
                cardIcon.setLayoutParams(new LinearLayout.LayoutParams(24, 24));

                ImageView icon = new ImageView(this);
                icon.setImageResource(iconRes);
                icon.setPadding(5, 5, 5, 5);
                icon.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                cardIcon.addView(icon);

                LinearLayout descLayout = new LinearLayout(this);
                descLayout.setOrientation(LinearLayout.VERTICAL);
                descLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                TextView tvKategori = new TextView(this);
                tvKategori.setText(t.kategori);
                tvKategori.setTextSize(12);
                tvKategori.setTypeface(null, Typeface.BOLD);
                tvKategori.setTextColor(getResources().getColor(R.color.black));

                TextView tvDeskripsi = new TextView(this);
                tvDeskripsi.setText(t.deskripsi);
                tvDeskripsi.setTextSize(11);
                tvDeskripsi.setTextColor(getResources().getColor(R.color.text_grey));

                descLayout.addView(tvKategori);
                descLayout.addView(tvDeskripsi);

                TextView tvHarga = new TextView(this);
                tvHarga.setText(formatCurrency(t.jumlah));
                tvHarga.setTextSize(11);
                tvHarga.setTypeface(null, Typeface.BOLD);
                tvHarga.setTextColor(getResources().getColor(R.color.black));

                itemLayout.addView(cardIcon);
                itemLayout.addView(descLayout);
                itemLayout.addView(tvHarga);

                containerRiwayat.addView(itemLayout);
            }
        }
    }

    private int getCategoryIcon(String kategori) {
        switch (kategori) {
            case "Makanan": return R.drawable.ic_cat_food;
            case "Transportasi": return R.drawable.ic_cat_transport;
            case "Belanja": return R.drawable.ic_cat_shopping;
            case "Hiburan": return R.drawable.ic_cat_movie;
            case "PulsaData": return R.drawable.ic_cat_shopping; // Ganti dengan ic_cat_phone jika sudah ada
            default: return R.drawable.ic_cat_food; // 👈 tambahkan ini!
        }
    }

    private int getCategoryColor(String kategori) {
        switch (kategori) {
            case "Makanan": return getResources().getColor(R.color.icon_makanan);
            case "Transportasi": return getResources().getColor(R.color.icon_transport);
            case "Belanja": return getResources().getColor(R.color.icon_belanja);
            case "Hiburan": return getResources().getColor(R.color.icon_hiburan);
            default: return getResources().getColor(R.color.icon_hiburan_dark);
        }
    }

    private String getMonthNumber(String bulanNama) {
        switch (bulanNama) {
            case "Januari": return "01";
            case "Februari": return "02";
            case "Maret": return "03";
            case "April": return "04";
            case "Mei": return "05";
            case "Juni": return "06";
            case "Juli": return "07";
            case "Agustus": return "08";
            case "September": return "09";
            case "Oktober": return "10";
            case "November": return "11";
            case "Desember": return "12";
            default: return "01";
        }
    }

    private String formatCurrency(float amount) {
        boolean isNegative = amount < 0;
        float absAmount = Math.abs(amount);
        String formatted = String.format("%,.2f", absAmount);
        String[] parts = formatted.split("\\.");
        String integerPart = parts[0].replace(",", ".");
        String decimalPart = parts.length > 1 ? "," + parts[1] : "";
        String result = "Rp. " + integerPart + decimalPart;
        return isNegative ? "-" + result : result;
    }

    public static class Transaksi {
        public String judul;
        public String deskripsi;
        public long jumlah;
        public String kategori;
        public String tanggal;
        public String waktu;

        public Transaksi(String judul, String deskripsi, long jumlah) {
            this.judul = judul;
            this.deskripsi = deskripsi;
            this.jumlah = jumlah;
        }
    }
}