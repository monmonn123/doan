package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private CheckBox chkRememberMe;

    // ĐỊA CHỈ API ĐĂNG NHẬP
    // Chạy máy ảo Android: 10.0.2.2
    // Chạy máy thật: Thay bằng IP của máy tính (VD: 192.168.1.X)
    private static final String LOGIN_URL = "http://10.0.2.2:4000/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. ÁNH XẠ (Đã khớp với file activity_login.xml của bạn)
        edtEmail = findViewById(R.id.edtUsername);      // ID bên XML là edtUsername
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvGoToRegister); // ID bên XML là tvGoToRegister
        chkRememberMe = findViewById(R.id.chkRememberMe);

        // 2. Xử lý sự kiện bấm nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // 3. Xử lý chuyển sang màn hình Đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    // --- HÀM LOGIN (GỬI DỮ LIỆU LÊN SERVER) ---
    private void loginUser(String email, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Đóng gói JSON
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                // Đọc phản hồi
                BufferedReader in;
                if (responseCode >= 200 && responseCode < 300) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // XỬ LÝ KẾT QUẢ TRÊN GIAO DIỆN CHÍNH
                runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONObject jsonResponse = new JSONObject(response.toString());

                            // Lấy Token và thông tin User
                            String token = jsonResponse.optString("token", "");

                            if (jsonResponse.has("user")) {
                                JSONObject userObj = jsonResponse.getJSONObject("user");

                                String firstName = userObj.optString("firstName", "");
                                String lastName = userObj.optString("lastName", "");
                                String fullName = (firstName + " " + lastName).trim();
                                if (fullName.isEmpty()) fullName = "Sinh viên";

                                String mssv = userObj.optString("username", "---");
                                String userEmail = userObj.optString("email", "");
                                String role = userObj.optString("role", "user");

                                // Lưu vào bộ nhớ máy (SharedPreferences)
                                SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("TOKEN", token);
                                editor.putString("FULL_NAME", fullName);
                                editor.putString("MSSV", mssv);
                                editor.putString("EMAIL", userEmail);
                                editor.putString("ROLE", role);
                                editor.putBoolean("IS_LOGGED_IN", true);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Xin chào " + fullName, Toast.LENGTH_SHORT).show();

                                // --- ĐIỀU HƯỚNG ---
                                // Tạm thời ai cũng cho vào MainActivity hết để không bị lỗi
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                // KHI NÀO CÓ ADMIN ACTIVITY THÌ MỞ COMMENT ĐOẠN NÀY RA:
                                /*
                                if ("admin".equals(role)) {
                                    intent = new Intent(LoginActivity.this, AdminActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                }
                                */

                                // Xóa lịch sử back stack
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Lỗi: Không tìm thấy thông tin user!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // Đăng nhập thất bại
                            JSONObject errorJson = new JSONObject(response.toString());
                            String message = errorJson.optString("message", "Đăng nhập thất bại");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối Server: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}