package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bottomNavigationView = findViewById(R.id.adminBottomNavigationView);

        // Mặc định tải trang Hỗ trợ (giống user với 2 tab)
        if (savedInstanceState == null) {
            loadFragment(new SupportFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_admin_qa);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_home) {
                // Trang chủ Admin
                return true;
            } else if (itemId == R.id.nav_admin_qa) {
                loadFragment(new SupportFragment()); // Mục này giờ hiện SupportFragment (Hỏi đáp + Nhắn tin)
                return true;
            } else if (itemId == R.id.nav_admin_notifications) {
                // Hiển thị Notification Activity hoặc Fragment
                startActivity(new Intent(this, NotificationActivity.class));
                return true;
            } else if (itemId == R.id.nav_admin_profile) {
                logout();
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
        finish();
    }
}