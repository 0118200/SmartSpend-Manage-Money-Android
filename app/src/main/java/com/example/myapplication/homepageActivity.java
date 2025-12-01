package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class homepageActivity extends AppCompatActivity {

    private LinearLayout navHome, navDailyBudget, navGraphic, navCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        // Handle window insets (notch/status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi item bottom navigation
        navHome = findViewById(R.id.bottom_navigation);
        navDailyBudget = findViewById(R.id.nav_daily_budget);
        navGraphic = findViewById(R.id.nav_graphic);
        navCategories = findViewById(R.id.nav_categories);

        // Handle klik
        navHome.setOnClickListener(v -> {
            // Sudah di halaman home, tidak perlu aksi
            // Opsional: scroll ke atas
            findViewById(R.id.main).post(() -> {
                findViewById(R.id.main).scrollTo(0, 0);
            });
        });

        navDailyBudget.setOnClickListener(v -> {
            startActivity(new Intent(homepageActivity.this, budgetingActivity.class));
        });

        navGraphic.setOnClickListener(v -> {
            startActivity(new Intent(homepageActivity.this, grafikActivity.class));
        });

        navCategories.setOnClickListener(v -> {
            startActivity(new Intent(homepageActivity.this, historyActivity.class));
        });

        // Inisialisasi tombol
        TextView btnAddPemasukan = findViewById(R.id.btn_add_pemasukan);

// Handler untuk tombol + Pemasukan
        btnAddPemasukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka activity pemasukan
                Intent intent = new Intent(homepageActivity.this, pemasukanActivity.class);
                startActivity(intent);
            }
        });
    }
}