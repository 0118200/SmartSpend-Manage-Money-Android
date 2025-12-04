// app/src/main/java/com/example/myapplication/pengeluaranActivity.java

package com.example.myapplication;

import static com.example.myapplication.utils.KategoriDetector.deteksiKategori;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.content.SharedPreferences;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class pengeluaranActivity extends AppCompatActivity {

    private EditText editJumlah, editDeskripsi;
    private Spinner spinnerKategori;
    private TextView tvTanggal;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pengeluaran);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view
        editJumlah = findViewById(R.id.edit_jumlah_pengeluaran);
        editDeskripsi = findViewById(R.id.edit_deskripsi);
        spinnerKategori = findViewById(R.id.spinner_kategori);
        tvTanggal = findViewById(R.id.tv_tanggal_pengeluaran);
        TextView btnSimpan = findViewById(R.id.btn_simpan_pengeluaran);

        // Set tanggal default hari ini
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        selectedDate = sdf.format(now.getTime());
        tvTanggal.setText(selectedDate);

        // Handler pilih tanggal
        tvTanggal.setOnClickListener(v -> showDatePicker());

        // Handler simpan
        btnSimpan.setOnClickListener(v -> simpanPengeluaran());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year1, month1, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                    selectedDate = sdf.format(selected.getTime());
                    tvTanggal.setText(selectedDate);
                }, year, month, day);
        dialog.show();
    }

    private void simpanPengeluaran() {
        String jumlahStr = editJumlah.getText().toString().trim();
        String deskripsi = editDeskripsi.getText().toString().trim();

        if (jumlahStr.isEmpty()) {
            Toast.makeText(this, "Masukkan jumlah pengeluaran!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (deskripsi.isEmpty()) {
            Toast.makeText(this, "Masukkan deskripsi!", Toast.LENGTH_SHORT).show();
            return;
        }

        double jumlah = 0;
        try {
            jumlah = Double.parseDouble(jumlahStr);
            if (jumlah <= 0) {
                Toast.makeText(this, "Jumlah harus lebih dari 0!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Masukkan angka yang valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Ambil dari spinner — user bisa pilih kategori sendiri
        String kategoriFinal = spinnerKategori.getSelectedItem().toString();
        if ("Pilih Kategori".equals(kategoriFinal)) {
            kategoriFinal = deteksiKategori(deskripsi);
        }

// Jika user tidak pilih, gunakan otomatis
        if ("Pilih Kategori".equals(kategoriFinal)) { // ganti "Pilih Kategori" dengan nilai default di spinner
            kategoriFinal = deteksiKategori(deskripsi);
        }
        // Format waktu
        String tanggalHariIni = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String periodeBulan = new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());

        // Format timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US).format(new Date());
        String key = "transaksi_" + timestamp;

// Simpan ke SharedPreferences (untuk history)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putString(key + "_judul", "Input Manual")
                .putString(key + "_deskripsi", deskripsi)
                .putLong(key + "_jumlah", (long) jumlah)
                .putString(key + "_kategori", kategoriFinal)
                .apply();

        Log.d("HISTORY_SAVE", "Data disimpan: " + key);
        // ✅ Jika mau, simpan juga ke FileHelper (riwayat global)
        // Transaksi transaksi = new Transaksi("Input Manual", deskripsi, (long) jumlah);
        // FileHelper.simpanTransaksi(this, transaksi);
        TransactionManager.recordTransactionWithCategory(this, (long) jumlah, kategoriFinal);
        Toast.makeText(this, "Pengeluaran disimpan!", Toast.LENGTH_SHORT).show();
        finish(); // kembali ke homepage → UI akan auto-refresh via onResume()
    }

    private String formatCurrency(double amount) {
        return "Rp. " + String.format("%,.0f", amount).replace(",", ".");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kembali ke homepage
        finish();
    }
}