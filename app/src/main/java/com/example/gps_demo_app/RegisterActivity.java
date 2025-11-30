package com.example.gps_demo_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUsername, etRegFullName, etRegPassword, etRegPasswordConfirm;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegFullName = findViewById(R.id.etRegFullName);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegPasswordConfirm = findViewById(R.id.etRegPasswordConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String fullName = etRegFullName.getText().toString().trim();
            String password = etRegPassword.getText().toString();
            String passwordConfirm = etRegPasswordConfirm.getText().toString();

            if(username.isEmpty() || fullName.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equals(passwordConfirm)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            String passwordHash = hashPassword(password);

            long id = dbHelper.addUser(username, passwordHash, fullName);
            if(id > 0) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private String hashPassword(String password) {
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
            return password;
        }
    }
}
