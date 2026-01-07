package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvProfileNameDisplay, tvMSSVDisplay, tvEmail, tvRole;
    private CardView btnChangePassword;
    private Button btnLogout;
    private FloatingActionButton fabChangeAvatar;
    private BottomNavigationView bottomNavigationView;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các View từ XML
        imgProfile = findViewById(R.id.imgProfile);
        tvProfileNameDisplay = findViewById(R.id.tvProfileNameDisplay);
        tvMSSVDisplay = findViewById(R.id.tvMSSVDisplay);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        fabChangeAvatar = findViewById(R.id.fabChangeAvatar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Kiểm tra bottomNavigationView để tránh lỗi crash nếu không tìm thấy ID trong layout
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_docs) {
                    startActivity(new Intent(this, DocumentListActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, BlogMainActivity.class));
                    finish();
                    return true;
                }
                return id == R.id.nav_profile;
            });
        }

        // Khởi tạo trình chọn ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            imgProfile.setImageURI(imageUri);
                            saveAvatarUri(imageUri.toString());
                            Toast.makeText(this, "Đã cập nhật ảnh đại diện!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        loadUserData();

        // Sự kiện click
        if (fabChangeAvatar != null) fabChangeAvatar.setOnClickListener(v -> openImagePicker());
        if (imgProfile != null) imgProfile.setOnClickListener(v -> openImagePicker());

        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng đổi mật khẩu đang được cập nhật", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void loadUserData() {
        // Sử dụng đúng tên SharedPreferences từ LoginActivity
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            String fullName = prefs.getString("FULL_NAME", "Người dùng");
            String role = prefs.getString("ROLE", "user");
            String userId = prefs.getString("USER_ID", "");
            
            // Lấy thêm MSSV hoặc Email nếu có lưu trong Preferences (tùy thuộc vào kết quả trả về từ server của bạn)
            // Vì API login của bạn chỉ trả về id, hoTen, role nên ta hiển thị các thông tin đó.
            
            if (tvProfileNameDisplay != null) tvProfileNameDisplay.setText(fullName);
            if (tvMSSVDisplay != null) tvMSSVDisplay.setText("ID người dùng: " + userId);
            
            // Vai trò
            if (tvRole != null) {
                if ("admin".equalsIgnoreCase(role)) {
                    tvRole.setText("Quản trị viên");
                } else {
                    tvRole.setText("Sinh viên");
                }
            }

            // Load Avatar đã lưu
            String avatarUri = prefs.getString(userId + "_avatarUri", null);
            if (avatarUri != null && imgProfile != null) {
                imgProfile.setImageURI(Uri.parse(avatarUri));
            }

        } else {
            // Nếu chưa đăng nhập thì quay về Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void saveAvatarUri(String uriString) {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);
        if (userId != null) {
            prefs.edit().putString(userId + "_avatarUri", uriString).apply();
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}