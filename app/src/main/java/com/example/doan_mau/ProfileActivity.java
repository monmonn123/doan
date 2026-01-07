package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView imgProfileAvatar;
    private TextView tvFullName, tvMssv, tvEmail;
    private Button btnUpdateProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các view theo ID mới trong activity_profile.xml
        imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        tvFullName = findViewById(R.id.tvProfileFullName);
        tvMssv = findViewById(R.id.tvProfileMssv);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserData();

        btnUpdateProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng cập nhật đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String fullName = prefs.getString("FULL_NAME", "Chưa có tên");
        String mssv = prefs.getString("USER_MSSV", "Chưa có MSSV");
        String email = prefs.getString("USER_EMAIL", "sinhvien@hcmute.edu.vn");

        tvFullName.setText(fullName);
        tvMssv.setText(mssv);
        tvEmail.setText(email);
        
        // Mặc định set ảnh nếu có logic lưu avatar thì load ở đây
        imgProfileAvatar.setImageResource(R.drawable.bogo);
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}