package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.myapplication.homepageActivity;

public class pemasukanActivity extends AppCompatActivity {

    private EditText editJumlah, editDanaDarurat;
    private TextView tvTanggalInput;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pemasukan);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view
        editJumlah = findViewById(R.id.edit_jumlah_pemasukan);
        editDanaDarurat = findViewById(R.id.edit_dana_darurat);
        tvTanggalInput = findViewById(R.id.tv_tanggal_input);

        // Handler untuk tombol kembali
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Handler untuk tombol simpan
        findViewById(R.id.btn_simpan_pemasukan).setOnClickListener(v -> simpanPemasukan());

        // Handler untuk pilih tanggal
        tvTanggalInput.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year1, month1, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                    selectedDate = sdf.format(selected.getTime());
                    tvTanggalInput.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }
    private void simpanPemasukan() {
        String jumlahStr = editJumlah.getText().toString().trim();
        String danaStr = editDanaDarurat.getText().toString().trim();

        if (jumlahStr.isEmpty() || danaStr.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parsing jumlah pemasukan
        double jumlah = 0;
        try {
            jumlah = Double.parseDouble(jumlahStr);
            if (jumlah <= 0) {
                Toast.makeText(this, "Jumlah pemasukan harus lebih dari 0!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Masukkan jumlah pemasukan yang valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parsing dana darurat (nominal langsung, bukan persentase)
        double danaDarurat = 0;
        try {
            danaDarurat = Double.parseDouble(danaStr);
            if (danaDarurat < 0) {
                Toast.makeText(this, "Dana darurat tidak boleh negatif!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (danaDarurat > jumlah) {
                Toast.makeText(this, "Dana darurat tidak boleh melebihi pemasukan!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Masukkan nominal dana darurat yang valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 💾 SIMPAN KE SHARED PREFERENCES
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putFloat("jumlah_pemasukan", (float) jumlah);
        editor.putFloat("dana_darurat_nominal", (float) danaDarurat); // simpan nominal langsung
        editor.putString("tanggal_input", selectedDate);
        editor.putFloat("dana_darurat_sisa", (float) danaDarurat);
        editor.apply();

        Toast.makeText(this,
                "Pemasukan disimpan!\nTotal: " + formatCurrency(jumlah) +
                        "\nDana Darurat: " + formatCurrency(danaDarurat) +
                        "\nTanggal: " + selectedDate,
                Toast.LENGTH_LONG).show();

        FileHelper.initSaldoBulanan(this, (float) jumlah);

        finish();
    }

    private String formatCurrency(double amount) {
        return "Rp. " + String.format("%,.0f", amount).replace(",", ".");
    }
}