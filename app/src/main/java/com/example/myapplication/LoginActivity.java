package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private LinearLayout btnLogin;
    private TextView txtDaftar;
    private TextView txtLupaSandi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Handle window insets (notch/status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi semua view
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtDaftar = findViewById(R.id.txtDaftar);
        txtLupaSandi = findViewById(R.id.txtLupaSandi);

        // Handle tombol login
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                inputEmail.setError("Email wajib diisi");
                inputEmail.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                inputPassword.setError("Kata sandi wajib diisi");
                inputPassword.requestFocus();
                return;
            }

            if (isValidUser(email, password)) {
                Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, homepageActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Email atau password salah", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Daftar Disini" → BUKA REGISTER
        txtDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Handle "Lupa Sandi?"
        txtLupaSandi.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Fitur lupa sandi belum tersedia", Toast.LENGTH_SHORT).show();
        });
    }

    // ✅ Method ini HARUS di luar onCreate
    private boolean isValidUser(String email, String password) {
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedEmail = sp.getString("email", "");
        String savedPassword = sp.getString("password", "");
        return email.equals(savedEmail) && password.equals(savedPassword);
    }
}