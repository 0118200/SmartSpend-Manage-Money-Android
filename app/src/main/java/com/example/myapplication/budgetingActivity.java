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
        int month = cal.get(Calendar.MONTH);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Ambil budget harian dari SharedPreferences
        float budgetHarian = calculateDailyBudget();

        // Generate item untuk setiap hari
        for (int day = 1; day <= daysInMonth; day++) {
            Calendar date = Calendar.getInstance();
            date.set(year, month, day);
            String formattedDate = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(date.getTime());
            String label = "Hari ke " + day + " : " + formattedDate;

            // ✅ Cukup satu baris ini
            float real = TransactionManager.getRealizationByDay(this, day);

            itemList.add(new DailyBudgetItem(label, budgetHarian, real));
        }

        adapter.notifyDataSetChanged();
    }

    private float calculateDailyBudget() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float pemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);
        return (pemasukan - danaDarurat) / 30f;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kembali ke homepage
        finish();
    }
}