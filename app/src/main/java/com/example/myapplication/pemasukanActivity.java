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
        String jumlah = editJumlah.getText().toString().trim();
        String danaDarurat = editDanaDarurat.getText().toString().trim();

        if (jumlah.isEmpty() || danaDarurat.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Simpan ke database / SharedPreferences
        Toast.makeText(this, "Pemasukan disimpan!\nJumlah: " + jumlah + "\nTanggal: " + selectedDate, Toast.LENGTH_LONG).show();

        // Kembali ke halaman sebelumnya setelah simpan
        finish();
    }
}