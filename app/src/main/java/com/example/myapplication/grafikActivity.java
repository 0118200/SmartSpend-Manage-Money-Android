// app/src/main/java/com/example/myapplication/grafikActivity.java

package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class grafikActivity extends AppCompatActivity {

    private PieChart pieChart;
    private Spinner spinnerBulan, spinnerTahun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grafik);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view
        pieChart = findViewById(R.id.pieChart);
        spinnerBulan = findViewById(R.id.spinner_bulan);
        spinnerTahun = findViewById(R.id.spinner_tahun);

        // Set listener untuk spinner
        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateGrafik();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateGrafik();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateGrafik(); // Load data pertama kali
    }

    private void updateGrafik() {
        String selectedBulanNama = spinnerBulan.getSelectedItem().toString();
        String selectedTahun = spinnerTahun.getSelectedItem().toString();

        // Konversi nama bulan ke angka (01-12)
        String bulanAngka = getMonthNumber(selectedBulanNama);
        String periode = selectedTahun + "-" + bulanAngka;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Ambil data per kategori — GUNAKAN KEY YANG SESUAI BULAN & TAHUN
        float makanan = prefs.getFloat("bulanan_Makanan_" + periode, 0f);
        float transport = prefs.getFloat("bulanan_Transportasi_" + periode, 0f);
        float belanja = prefs.getFloat("bulanan_Belanja_" + periode, 0f);
        float hiburan = prefs.getFloat("bulanan_Hiburan_" + periode, 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f) - prefs.getFloat("dana_darurat_sisa", 0f);

        // Hitung total pengeluaran
        float total = makanan + transport + belanja + hiburan + danaDarurat;
        if (total == 0) total = 1;

        // Hitung persentase
        float pctMakanan = (makanan / total) * 100;
        float pctTransport = (transport / total) * 100;
        float pctBelanja = (belanja / total) * 100;
        float pctHiburan = (hiburan / total) * 100;
        float pctDarurat = (danaDarurat / total) * 100;

        // Format currency
        java.util.function.Function<Float, String> format = amount ->
                "Rp." + String.format("%,d", Math.round(amount)).replace(",", ".");

        // Cari kategori terbesar untuk tampilan tengah
        String[] kategori = {"Makanan", "Transportasi", "Belanja", "Hiburan", "Dana Darurat"};
        float[] nilaiPersen = {pctMakanan, pctTransport, pctBelanja, pctHiburan, pctDarurat};

        int maxIdx = 0;
        for (int i = 1; i < nilaiPersen.length; i++) {
            if (nilaiPersen[i] > nilaiPersen[maxIdx]) maxIdx = i;
        }

        // Tampilkan di tengah: "82.3%\nDana Darurat"
        String centerText = String.format("%.1f", nilaiPersen[maxIdx]) + "%\n" + kategori[maxIdx];

        // PieChart entries — hanya nama kategori (tanpa persentase di dalam pie)
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(pctMakanan, "Makanan"));
        entries.add(new PieEntry(pctTransport, "Transportasi"));
        entries.add(new PieEntry(pctBelanja, "Belanja"));
        entries.add(new PieEntry(pctHiburan, "Hiburan"));
        entries.add(new PieEntry(pctDarurat, "Dana Darurat"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK); // teks di luar pie

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(65f);
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();

        // Update legend — tambahkan persentase di sini jika mau
        ((TextView) findViewById(R.id.tv_legend_makanan)).setText(":  " + format.apply(makanan) + " (" + String.format("%.1f", pctMakanan) + "%)");
        ((TextView) findViewById(R.id.tv_legend_transport)).setText(":  " + format.apply(transport) + " (" + String.format("%.1f", pctTransport) + "%)");
        ((TextView) findViewById(R.id.tv_legend_belanja)).setText(":  " + format.apply(belanja) + " (" + String.format("%.1f", pctBelanja) + "%)");
        ((TextView) findViewById(R.id.tv_legend_hiburan)).setText(":  " + format.apply(hiburan) + " (" + String.format("%.1f", pctHiburan) + "%)");
        ((TextView) findViewById(R.id.tv_legend_darurat)).setText(":  " + format.apply(danaDarurat) + " (" + String.format("%.1f", pctDarurat) + "%)");

        // Cari terbesar/terkecil berdasarkan NILAI UANG ASLI (bukan persentase)
        float[] nilaiRupiah = {makanan, transport, belanja, hiburan, danaDarurat};

        int minIdx = 0;
        for (int i = 1; i < nilaiRupiah.length; i++) {
            if (nilaiRupiah[i] > nilaiRupiah[maxIdx]) maxIdx = i;
            if (nilaiRupiah[i] < nilaiRupiah[minIdx]) minIdx = i;
        }

// Tampilkan nilai asli (bukan total)
        ((TextView) findViewById(R.id.tv_pengeluaran_terbesar))
                .setText("Pengeluaran Terbesar  :  " + kategori[maxIdx] + " (" + format.apply(nilaiRupiah[maxIdx]) + ")");

        ((TextView) findViewById(R.id.tv_pengeluaran_terkecil))
                .setText("Pengeluaran Terkecil  :  " + kategori[minIdx] + " (" + format.apply(nilaiRupiah[minIdx]) + ")");

    }

    // Helper: konversi nama bulan ke angka
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
}