package com.example.doan_mau;

import android.content.Intent;
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

public class ConfirmCodeActivity extends AppCompatActivity {

    private EditText edtConfirmationCode;
    private Button btnConfirm;
    private TextView tvInstruction, tvResendCode;

    private final String MOCK_CODE = "123456";

    // ĐỊA CHỈ SERVER (QUAN TRỌNG):
    // Dựa vào file server.js và authController.js của bạn, đường dẫn chuẩn là:
    private static final String REGISTER_URL = "http://10.0.2.2:4000/api/auth/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);

        edtConfirmationCode = findViewById(R.id.edtConfirmationCode);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvResendCode = findViewById(R.id.tvResendCode);

        // Nhận dữ liệu từ RegistrationActivity
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        String userPassword = getIntent().getStringExtra("USER_PASSWORD");
        String userFullName = getIntent().getStringExtra("USER_FULL_NAME");
        // Nhận Username (chính là MSSV)
        String userUsername = getIntent().getStringExtra("USER_USERNAME");

        if (userEmail != null) {
            tvInstruction.setText("Mã xác thực mặc định là 123456. Email: " + userEmail);
        }

        btnConfirm.setOnClickListener(v -> {
            String enteredCode = edtConfirmationCode.getText().toString();
            if (enteredCode.equals(MOCK_CODE)) {
                // Mã đúng -> Gọi API gửi lên Server
                registerUserOnServer(userUsername, userEmail, userPassword, userFullName);
            } else {
                Toast.makeText(this, "Mã xác thực không đúng!", Toast.LENGTH_SHORT).show();
            }
        });

        tvResendCode.setOnClickListener(v -> {
            Toast.makeText(this, "Mã xác thực là: 123456", Toast.LENGTH_SHORT).show();
        });
    }

    // Hàm gọi API chạy ngầm
    // Tìm đến hàm registerUserOnServer và sửa phần JSON Body như sau:
    private void registerUserOnServer(String username, String email, String password, String fullName) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            HttpURLConnection conn = null; // Khai báo ngoài để đảm bảo ngắt kết nối an toàn
            try {
                URL url = new URL(REGISTER_URL);
                conn = (HttpURLConnection) url.openConnection();

                // BƯỚC 1: Cài đặt thuộc tính TRƯỚC KHI kết nối
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // BƯỚC 2: Đóng gói JSON (Sửa lỗi studentId đỏ lòm)
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("mssv", username); // Dùng đúng biến username truyền từ trên xuống
                jsonBody.put("hoTen", fullName);
                jsonBody.put("email", email);
                jsonBody.put("password", password);

                // BƯỚC 3: Mở luồng và gửi dữ liệu
                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes("UTF-8"));
                os.close();

                // BƯỚC 4: Nhận kết quả từ Server
                int responseCode = conn.getResponseCode();

                // Xóa bỏ tất cả các dòng setRequestProperty thừa ở đoạn này

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

                runOnUiThread(() -> {
                    if (responseCode == 200 || responseCode == 201) {
                        Toast.makeText(ConfirmCodeActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ConfirmCodeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ConfirmCodeActivity.this, "Lỗi Server: " + response.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ConfirmCodeActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                if (conn != null) conn.disconnect(); // Ngắt kết nối để giải phóng tài nguyên
            }
        });
    }
}