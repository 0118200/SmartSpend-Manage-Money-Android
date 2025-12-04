package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;import android.preference.PreferenceManager;
import android.content.SharedPreferences;  import java.util.prefs.Preferences;
import com.example.myapplication.TransactionManager;

public class budgetingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DailyBudgetAdapter adapter;
    private List<DailyBudgetItem> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_daily_budget);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DailyBudgetAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // Generate data harian
        loadDailyData();
    }

    private void loadDailyData() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH); // Januari = 0, jadi Desember = 11
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        float budgetHarian = calculateDailyBudget();
        itemList.clear(); // pastikan list kosong dulu

        for (int day = 1; day <= daysInMonth; day++) {
            // Format tanggal sebagai "yyyy-MM-dd" (misal: "2025-12-03")
            String tanggal = String.format("%04d-%02d-%02d", year, month + 1, day); // month+1 karena Calendar mulai dari 0

            // Ambil realisasi untuk tanggal ini
            float realisasi = TransactionManager.getRealizationByDate(this, tanggal);

            // Format label tampilan
            Calendar date = Calendar.getInstance();
            date.set(year, month, day);
            String formattedDate = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(date.getTime());
            String label = "Hari ke " + day + " : " + formattedDate;

            itemList.add(new DailyBudgetItem(label, budgetHarian, realisasi));
        }

        adapter.notifyDataSetChanged();
    }

    private float calculateDailyBudget() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float pemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);
        float saldoTersedia = pemasukan - danaDarurat;

        // Jika saldo tersedia negatif, set jadi 0
        if (saldoTersedia < 0) saldoTersedia = 0;

        // Hitung budget harian — bulatkan ke bawah (floor) agar tidak overbudget
        return (float) Math.floor(saldoTersedia / 30);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kembali ke homepage
        finish();
    }
}