package com.example.smartspend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.model.Transaksi;

import java.util.List;

public class RiwayatActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi); // Pastikan layout ini benar

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<Transaksi> list = FileHelper.ambilSemuaTransaksi(this);

        TextView tvEmptyState = findViewById(R.id.tv_empty_state);

        if (list.isEmpty()) {
            rv.setVisibility(View.GONE); // Sembunyikan RecyclerView
            tvEmptyState.setVisibility(View.VISIBLE); // Tampilkan pesan kosong
        } else {
            rv.setVisibility(View.VISIBLE); // Tampilkan RecyclerView
            tvEmptyState.setVisibility(View.GONE); // Sembunyikan pesan kosong
        }

        TransaksiAdapter adapter = new TransaksiAdapter(list);
        rv.setAdapter(adapter);

        Button btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(v -> finish());

//        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                finish(); // atau lakukan hal lain
//            }
//        });
    }


    @Override
    public void onBackPressed() {
        // Gunakan ini jika kamu ingin tetap mengikuti flow normal Android
        super.onBackPressed();
    }
}