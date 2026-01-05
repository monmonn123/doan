package com.example.doan_mau;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Thiết lập video nền
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anh_nen);
        videoBackground.setVideoURI(uri);
        videoBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0);
        });
        videoBackground.start();

        btnRegister.setOnClickListener(v -> {
            if (validateInput()) {
                String fullName = edtFullName.getText().toString().trim();
                String studentId = edtStudentId.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString();

                Intent intent = new Intent(RegistrationActivity.this, ConfirmCodeActivity.class);
                intent.putExtra("USER_EMAIL", email);
                intent.putExtra("USER_PASSWORD", password);
                intent.putExtra("USER_FULL_NAME", fullName);
                intent.putExtra("USER_STUDENT_ID", studentId);
                startActivity(intent);
            }
        });
    }

    private boolean validateInput() {
        String fullName = edtFullName.getText().toString().trim();
        String studentId = edtStudentId.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (fullName.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Mật khẩu phải dài ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean isPasswordStrong(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu lại video khi quay lại màn hình
        videoBackground.start();
    }
}
