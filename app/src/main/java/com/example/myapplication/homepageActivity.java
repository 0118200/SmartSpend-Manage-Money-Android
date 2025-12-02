package com.example.myapplication;

import java.util.Calendar;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
public class homepageActivity extends AppCompatActivity {

    private LinearLayout navHome, navDailyBudget, navGraphic, navCategories;
    private TextView tvSaldoBulanIni, tvDanaDarurat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        loadKategoriPengeluaran();
        loadDataFromPrefs(); // 👈 Ini penting!
        updateBudgetHarian();


        tvSaldoBulanIni = findViewById(R.id.tv_saldo_bulan_ini); // Ganti dengan ID TextView di layoutmu
        tvDanaDarurat = findViewById(R.id.tv_dana_darurat);       // Ganti dengan ID TextView di layoutmu

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
        navHome.setOnClickListener(v -> {
            findViewById(R.id.main).post(() -> findViewById(R.id.main).scrollTo(0, 0));
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
        tvSaldoBulanIni = findViewById(R.id.tv_saldo_bulan_ini);
        CardView btnAddPengeluaran = findViewById(R.id.btn_add_pengeluaran);
        tvDanaDarurat = findViewById(R.id.tv_dana_darurat);

// Handler untuk tombol + Pemasukan
        btnAddPemasukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka activity pemasukan
                Intent intent = new Intent(homepageActivity.this, pemasukanActivity.class);
                startActivity(intent);
            }
        });

        // TOMBOL UJI CEPAT (hapus nanti)
        TextView btnTest = findViewById(R.id.btn_add_pemasukan);
        btnTest.setOnLongClickListener(v -> {
            // Simulasi pengeluaran Rp45.000
            TransactionManager.recordTransaction(this, 45000);
            Toast.makeText(this, "✅ +Rp45.000 ke hari ini", Toast.LENGTH_SHORT).show();
            updateBudgetHarian(); // refresh UI
            return true;
        });




        btnAddPengeluaran.setOnClickListener(v -> {
            startActivity(new Intent(homepageActivity.this, pengeluaranActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromPrefs(); // Muat ulang data setiap kali masuk ke homepage
        loadKategoriPengeluaran();
        updateBudgetHarian();
    }

    private void loadDataFromPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        float jumlahPemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);
        String tanggalInput = prefs.getString("tanggal_input", "Belum ada data");

        // Hitung saldo tersedia = pemasukan - dana darurat
        float saldoTersedia = jumlahPemasukan - danaDarurat;

        // Format ke string
        String saldoFormatted = formatCurrency(saldoTersedia);
        String danaFormatted = formatCurrency(danaDarurat);

        // Tampilkan ke TextView
        TextView tvSaldo = findViewById(R.id.tv_saldo_bulan_ini);
        TextView tvDana = findViewById(R.id.tv_dana_darurat);
        TextView tvTanggal = findViewById(R.id.tv_tanggal_saldo);

        tvSaldo.setText(saldoFormatted);
        tvDana.setText(danaFormatted);
        tvTanggal.setText(tanggalInput);
    }

    // Tambahkan method formatCurrency (copy dari pemasukanActivity jika perlu)
    private String formatCurrency(double amount) {
        return "Rp. " + String.format("%,.0f", amount).replace(",", ".");
    }

    private void loadKategoriPengeluaran() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Ambil total dari SharedPreferences dan tampilkan
        float makanan = prefs.getFloat("total_kategori_Makanan", 0f);
        float transport = prefs.getFloat("total_kategori_Transportasi", 0f);
        float belanja = prefs.getFloat("total_kategori_Belanja", 0f);
        float hiburan = prefs.getFloat("total_kategori_Hiburan", 0f);

        ((TextView) findViewById(R.id.tv_kategori_makanan)).setText(formatCurrency(makanan));
        ((TextView) findViewById(R.id.tv_kategori_transportasi)).setText(formatCurrency(transport));
        ((TextView) findViewById(R.id.tv_kategori_belanja)).setText(formatCurrency(belanja));
        ((TextView) findViewById(R.id.tv_kategori_hiburan)).setText(formatCurrency(hiburan));
    }

    private String formatCurrency(float amount) {
        return "Rp. " + String.format("%,.0f", amount).replace(",", ".");
    }

    private void updateBudgetHarian() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Ambil data pemasukan & dana darurat
        float jumlahPemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);

        // Hitung budget harian (TETAP, tidak berubah)
        float saldoTersedia = jumlahPemasukan - danaDarurat;
        float budgetHarian = saldoTersedia / 30f;

        // Ambil realisasi HARI INI (dari notifikasi bank)
        float terpakai = TransactionManager.getTodayRealization(this);

        // Hitung sisa (tidak boleh negatif — jika overbudget, sisa = 0)
        float sisa = budgetHarian - terpakai;
        if (sisa < 0) {
            sisa = 0; // Jangan sampai minus — sisa maksimal 0
        }

        // Info hari ini
        Calendar today = Calendar.getInstance();
        int hariKe = today.get(Calendar.DAY_OF_MONTH);
        int totalHari = today.getActualMaximum(Calendar.DAY_OF_MONTH);
        String hariText = "Hari ke " + hariKe + " dari " + totalHari + " hari";

        // Update UI
        TextView tvHariKe = findViewById(R.id.tv_hari_ke);
        TextView tvBudget = findViewById(R.id.tv_budget_harian);
        TextView tvTerpakai = findViewById(R.id.tv_terpakai);
        TextView tvSisa = findViewById(R.id.tv_sisa_budget);

        tvHariKe.setText(hariText);
        tvBudget.setText(formatCurrency(budgetHarian));
        tvTerpakai.setText(formatCurrency(terpakai));
        tvSisa.setText(formatCurrency(sisa));

        // Warna sisa: merah jika overbudget (terpakai > budget), hijau jika masih ada sisa
        if (terpakai > budgetHarian) {
            tvSisa.setTextColor(ContextCompat.getColor(this, R.color.text_red)); // overbudget
        } else {
            tvSisa.setTextColor(ContextCompat.getColor(this, R.color.black)); // normal
        }
    }


}