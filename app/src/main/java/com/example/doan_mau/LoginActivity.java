package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText edtMssv, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private static final String LOGIN_URL = "http://10.0.2.2:4000/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtMssv = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String mssv = edtMssv.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (mssv.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập MSSV và mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(mssv, password);
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrationActivity.class));
        });
    }

    private void loginUser(String mssv, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mssv", mssv);
                jsonParam.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) response.append(line);
                in.close();

                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (responseCode == 200) {
                            String token = jsonResponse.optString("token");
                            JSONObject userObj = jsonResponse.getJSONObject("user");

                            String userId = userObj.getString("id");
                            String fullName = userObj.optString("hoTen", "Sinh viên");
                            String role = userObj.optString("role", "user");

                            SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
                            prefs.edit().putString("USER_ID", userId)
                                    .putString("USER_TOKEN", token)
                                    .putString("FULL_NAME", fullName)
                                    .putString("ROLE", role)
                                    .putBoolean("IS_LOGGED_IN", true).apply();

                            Toast.makeText(this, "Chào mừng " + fullName, Toast.LENGTH_SHORT).show();

                            // PHÂN QUYỀN VÀO TRANG CHỦ HOẶC ADMIN
                            if ("admin".equals(role)) {
                                startActivity(new Intent(this, AdminActivity.class));
                            } else {
                                startActivity(new Intent(this, MainActivity.class));
                            }
                            finish();
                        } else {
                            Toast.makeText(this, jsonResponse.optString("message", "Lỗi"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}