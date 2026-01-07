package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edtMssv, edtPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtMssv = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> performLogin());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));

        // XỬ LÝ QUÊN MẬT KHẨU
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển tới trang nhận OTP qua mail...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String mssv = edtMssv.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (mssv.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("mssv", mssv);
        body.put("password", password);

        RetrofitClient.getApi().loginUser(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> bodyData = response.body();
                    Map<String, Object> user = (Map<String, Object>) bodyData.get("user");
                    String role = (String) user.get("role");

                    // LƯU PHIÊN ĐĂNG NHẬP
                    SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
                    prefs.edit().putString("USER_ID", (String) user.get("id"))
                            .putString("ROLE", role)
                            .putString("FULL_NAME", (String) user.get("hoTen"))
                            .putString("USER_MSSV", (String) user.get("mssv"))
                            .putString("USER_EMAIL", (String) user.get("email"))
                            .putBoolean("IS_LOGGED_IN", true).apply();

                    // PHÂN QUYỀN ĐIỀU HƯỚNG
                    if ("admin".equals(role)) startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    else if ("manager".equals(role)) startActivity(new Intent(LoginActivity.this, ManagerActivity.class));
                    else startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}