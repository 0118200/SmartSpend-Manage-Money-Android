package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import java.util.Date;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
    private float budgetHarianTetap = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        loadKategoriPengeluaran();
        loadDataFromPrefs(); // 👈 Ini penting!
        updateBudgetHarian();
        cekResetBulanan();




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
// Inisialisasi bell icon
        ImageView bellIcon = findViewById(R.id.bell_icon);
        TextView bellBadge = findViewById(R.id.bell_badge);

// Set click listener
        bellIcon.setOnClickListener(v -> {
            startActivity(new Intent(homepageActivity.this, notifikasiActivity.class));
        });

// Cek apakah ada notifikasi penting
        if (shouldShowNotification()) {
            bellBadge.setVisibility(View.VISIBLE);
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
            bellIcon.startAnimation(pulse);
        } else {
            bellBadge.setVisibility(View.GONE);
        }
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

        btnAddPengeluaran.setOnClickListener(v -> {
            startActivity(new Intent(homepageActivity.this, pengeluaranActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromPrefs();
        updateBudgetHarian();
        loadKategoriPengeluaran();
    }

    private boolean shouldShowNotification() {
        // 1. Cek apakah ada pengeluaran hari ini
        float todaySpent = TransactionManager.getTotalTodayRealization(this);
        if (todaySpent == 0) {
            return true; // Belum input hari ini
        }

        // 2. Cek apakah mendekati batas bulanan
        float totalBulanan = getTotalBulanan();
        float pemasukan = PreferenceManager.getDefaultSharedPreferences(this)
                .getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = PreferenceManager.getDefaultSharedPreferences(this)
                .getFloat("dana_darurat_nominal", 0f);
        float saldoTersedia = pemasukan - danaDarurat;
        if (saldoTersedia > 0) {
            float persen = (totalBulanan / saldoTersedia) * 100;
            if (persen >= 80) {
                return true; // Mendekati batas
            }
        }

        // 3. Cek kategori yang hampir over
        String[] kategoriList = {"Makanan", "Transportasi", "Belanja", "Hiburan"};
        float budgetPerKategori = saldoTersedia / kategoriList.length;
        for (String k : kategoriList) {
            float spent = getBulananKategori(k);
            if (budgetPerKategori > 0) {
                float persenKategori = (spent / budgetPerKategori) * 100;
                if (persenKategori >= 80) {
                    return true; // Kategori hampir over
                }
            }
        }

        // 4. Cek dana darurat
        float danaSisa = PreferenceManager.getDefaultSharedPreferences(this)
                .getFloat("dana_darurat_sisa", 0f);
        if (danaSisa <= 50000) { // Jika sisa kurang dari Rp 50.000
            return true; // Dana darurat hampir habis
        }

        return false; // Tidak ada notifikasi penting
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

    private void loadDataFromPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        float jumlahPemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);
        String tanggalInput = prefs.getString("tanggal_input", "Belum ada data");
        float saldoBulanan = FileHelper.ambilSaldoBulananTersedia(this); // 👈 INI YANG DITAMPILKAN DI TV_SALDO_BULAN_INI

        // ✅ Hitung budget harian sekali — ini nilai TETAP
        float saldoTersedia = jumlahPemasukan - danaDarurat;
        budgetHarianTetap = saldoTersedia / 30f;

        // Format ke string
        String saldoFormatted = formatCurrency(saldoTersedia); // ❌ INI YANG SEHARUSNYA DITAMPILKAN??
        String danaFormatted = formatCurrency(danaDarurat);

        // Tampilkan ke TextView
        TextView tvSaldo = findViewById(R.id.tv_saldo_bulan_ini);
        TextView tvDana = findViewById(R.id.tv_dana_darurat);
        TextView tvTanggal = findViewById(R.id.tv_tanggal_saldo);

        tvSaldo.setText(saldoFormatted); // ❗️INI SALAH! HARUSNYA saldoBulanan, BUKAN saldoTersedia!
        tvDana.setText(danaFormatted);
        tvTanggal.setText(tanggalInput);
    }

    // Tambahkan method formatCurrency (copy dari pemasukanActivity jika perlu)
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
    private void loadKategoriPengeluaran() {
        // Baca data HARI INI saja
        float makanan = TransactionManager.getTodayByCategory(this, "Makanan");
        float transport = TransactionManager.getTodayByCategory(this, "Transportasi");
        float belanja = TransactionManager.getTodayByCategory(this, "Belanja");
        float hiburan = TransactionManager.getTodayByCategory(this, "Hiburan");

        ((TextView) findViewById(R.id.tv_kategori_makanan)).setText(formatCurrency(makanan));
        ((TextView) findViewById(R.id.tv_kategori_transportasi)).setText(formatCurrency(transport));
        ((TextView) findViewById(R.id.tv_kategori_belanja)).setText(formatCurrency(belanja));
        ((TextView) findViewById(R.id.tv_kategori_hiburan)).setText(formatCurrency(hiburan));
    }


    public static float getTodayByCategory(Context context, String kategori) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat("harian_" + kategori + "_" + today, 0f); // ✅ sama dengan key simpan
    }

    private void updateBudgetHarian() {
        // Ambil realisasi HARI INI
        float terpakai = TransactionManager.getTotalTodayRealization(this);

        // Gunakan budget harian TETAP
        float budgetHarian = calculateDailyBudget();

        // Hitung sisa
        float sisa = budgetHarian - terpakai;

        // Ambil dana darurat sisa
        float danaDaruratSisa = getDanaDaruratSisa();

        TextView tvDanaDaruratSisa = findViewById(R.id.tv_dana_darurat);
        tvDanaDaruratSisa.setText("Dana Darurat Sisa: \n" + formatCurrency(getDanaDaruratSisa()));

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
//        TextView tvDanaDaruratSisa = findViewById(R.id.tv_dana_darurat_sisa); // tambahkan TextView ini di layout

        tvHariKe.setText(hariText);
        tvBudget.setText(formatCurrency(budgetHarian));
        tvTerpakai.setText(formatCurrency(terpakai));
        tvSisa.setText(formatCurrency(sisa));
//        tvDanaDaruratSisa.setText(formatCurrency(danaDaruratSisa));

        // Warna sisa: merah jika overbudget
        if (terpakai > budgetHarian) {
            tvSisa.setTextColor(ContextCompat.getColor(this, R.color.text_red));
        } else {
            tvSisa.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    private float calculateDailyBudget() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float pemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
        float danaDarurat = prefs.getFloat("dana_darurat_nominal", 0f);
        float saldo = pemasukan - danaDarurat;
        if (saldo < 0) saldo = 0;

        Calendar cal = Calendar.getInstance();
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return saldo / days;
    }

    private float getDanaDaruratSisa() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getFloat("dana_darurat_sisa", 0f);
    }

    private void cekResetBulanan() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lastReset = prefs.getString("last_reset_bulan", "");
        String currentBulan = new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());

        if (!lastReset.equals(currentBulan)) {
            // Ambil pemasukan awal bulan
            float pemasukan = prefs.getFloat("jumlah_pemasukan", 0f);
            FileHelper.initSaldoBulanan(this, pemasukan);
            prefs.edit().putString("last_reset_bulan", currentBulan).apply();
        }
    }
}