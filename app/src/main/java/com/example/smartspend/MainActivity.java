package com.example.smartspend;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etJumlah;
    private TextView tvSaldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAktif = findViewById(R.id.btn_aktifkan);
        Button btnRiwayat = findViewById(R.id.btn_riwayat);
        Button btnTambahDana = findViewById(R.id.btn_tambah_dana); // Tambahkan tombol ini
        etJumlah = findViewById(R.id.et_jumlah); // Input jumlah
        tvSaldo = findViewById(R.id.tv_saldo); // Tampilan saldo

        // Tampilkan saldo awal
        long saldoAwal = FileHelper.ambilSaldo(this);
        tvSaldo.setText("Saldo: Rp" + saldoAwal);

        btnAktif.setOnClickListener(v -> {
            if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Akses notifikasi aktif", Toast.LENGTH_SHORT).show();
            }
        });

        btnRiwayat.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RiwayatActivity.class));
        });

        // 🔥 Fungsi tambah dana
        btnTambahDana.setOnClickListener(v -> {
            String input = etJumlah.getText().toString();
            if (!input.isEmpty()) {
                try {
                    long jumlah = Long.parseLong(input);
                    FileHelper.tambahSaldo(this, jumlah);
                    Toast.makeText(this, "Dana ditambahkan!", Toast.LENGTH_SHORT).show();

                    // Update tampilan saldo
                    long saldoBaru = FileHelper.ambilSaldo(this);
                    tvSaldo.setText("Saldo: Rp" + saldoBaru);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Masukkan jumlah dana", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update tampilan saldo saat kembali ke MainActivity (misalnya setelah kembali dari Riwayat)
        long saldoBaru = FileHelper.ambilSaldo(this);
        tvSaldo.setText("Saldo: Rp" + saldoBaru);
    }
}