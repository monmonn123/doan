package com.example.doan_mau;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap; // Hết đỏ Map
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private EditText edtFullName, edtStudentId, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private VideoView videoBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edtFullName = findViewById(R.id.edtFullName);
        edtStudentId = findViewById(R.id.edtStudentId);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        videoBackground = findViewById(R.id.videoBackground);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anh_nen);
        videoBackground.setVideoURI(uri);
        videoBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0);
        });
        videoBackground.start();

        btnRegister.setOnClickListener(v -> {
            if (validateInput()) performRegistration();
        });
    }

    private void performRegistration() {
        Map<String, String> regData = new HashMap<>();
        regData.put("hoTen", edtFullName.getText().toString().trim());
        regData.put("mssv", edtStudentId.getText().toString().trim());
        regData.put("email", edtEmail.getText().toString().trim());
        regData.put("password", edtPassword.getText().toString());

        // GỌI ĐĂNG KÝ TRỰC TIẾP (KHÔNG OTP)
        RetrofitClient.getApi().registerUser(regData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Lỗi: MSSV hoặc Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegistrationActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        if (edtFullName.getText().toString().isEmpty() || edtStudentId.getText().toString().isEmpty() ||
                edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() { super.onResume(); videoBackground.start(); }
}