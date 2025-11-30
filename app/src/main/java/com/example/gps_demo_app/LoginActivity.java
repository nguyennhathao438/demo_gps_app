package com.example.gps_demo_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvGoToRegister;
    private Button btnLogin;

    private DBHelper dbHelper; // class SQLite bạn đã tạo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase();
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bạn nên mã hóa password trước khi check (vd SHA-256)
            String passwordHash = hashPassword(password);

            boolean success = dbHelper.checkLogin(username, passwordHash);
            if(success) {
                int userId = dbHelper.getUserIdByUsername(username);

                // Lưu userId vào SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("user_id", userId);
                editor.putString("username", username); // nếu cần
                editor.apply();

                // Chuyển sang màn hình chính
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoToRegister.setOnClickListener(v -> {
            // Chuyển sang màn hình đăng ký
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private String hashPassword(String password) {
        // Đơn giản demo hash SHA-256
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
            return password; // fallback (không nên dùng thực tế)
        }
    }
}

