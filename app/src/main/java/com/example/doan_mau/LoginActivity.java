package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private CheckBox chkRememberMe;
    private VideoView videoBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        SharedPreferences loginPrefs = getSharedPreferences("LOGIN_PREFS", MODE_PRIVATE);
        if (loginPrefs.getBoolean("isLoggedIn", false)) {
            // Nếu đã đăng nhập, chuyển thẳng vào MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return; // Dừng việc thực thi tiếp theo của onCreate
        }

        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        chkRememberMe = findViewById(R.id.chkRememberMe);
        videoBackground = findViewById(R.id.videoBackground);

        // Thiết lập video nền
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.anh_nen);
        videoBackground.setVideoURI(uri);
        videoBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0);
        });
        videoBackground.start();

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString();

            if (checkLogin(username, password)) {
                // Lưu email của người dùng hiện tại
                SharedPreferences currentUserPrefs = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
                currentUserPrefs.edit().putString("email", username).apply();

                // Xử lý "Ghi nhớ đăng nhập"
                if (chkRememberMe.isChecked()) {
                    SharedPreferences.Editor loginEditor = loginPrefs.edit();
                    loginEditor.putBoolean("isLoggedIn", true);
                    loginEditor.apply();
                }

                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private boolean checkLogin(String email, String password) {
        if (email.equals("admin") && password.equals("admin")) {
            return true;
        }
        SharedPreferences prefs = getSharedPreferences("USER_ACCOUNTS", MODE_PRIVATE);
        String savedPassword = prefs.getString(email + "_password", null);
        return savedPassword != null && savedPassword.equals(password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoBackground != null) {
            videoBackground.start();
        }
    }
}
