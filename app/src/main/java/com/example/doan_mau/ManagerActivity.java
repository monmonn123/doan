package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // Sử dụng chung layout với admin vì cùng có bottom nav và container

        bottomNavigationView = findViewById(R.id.adminBottomNavigationView);

        if (savedInstanceState == null) {
            loadFragment(new SupportFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_admin_qa);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_home) {
                // Trang chủ Manager
                return true;
            } else if (itemId == R.id.nav_admin_qa) {
                loadFragment(new SupportFragment());
                return true;
            } else if (itemId == R.id.nav_admin_notifications) {
                // Sửa ở đây: Kiểm tra vai trò để load fragment phù hợp
                SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
                String userRole = prefs.getString("USER_ROLE", "user");

                if (userRole.equals("admin") || userRole.equals("manager")) {
                    loadFragment(new NotificationFragment());
                } else {
                    loadFragment(new UserNotificationFragment());
                }
                return true;
            } else if (itemId == R.id.nav_admin_profile) {
                // Trang Profile cho Manager (làm sau)
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
}