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

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirmPassword;
    private LinearLayout btnRegister;
    private TextView txtBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi view
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

        // Handle tombol Daftar
        btnRegister.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmPassword = inputConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                inputEmail.setError("Email/username wajib diisi");
                inputEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                inputPassword.setError("Kata sandi wajib diisi");
                inputPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                inputConfirmPassword.setError("Kata sandi tidak cocok");
                inputConfirmPassword.requestFocus();
                return;
            }

            // ✅ Simpan data ke SharedPreferences (untuk prototipe)
            saveUser(email, password);

            Toast.makeText(RegisterActivity.this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show();

            // Pindah ke halaman login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tutup register agar tidak bisa kembali
        });

        // Handle "Masuk disini"
        txtBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Simpan data user sederhana ke SharedPreferences
    private void saveUser(String email, String password) {
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }
}