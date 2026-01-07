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

        if (savedInstanceState == null) {
            loadFragment(new SupportFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_admin_qa);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_home) {
                // Có thể thêm AdminHomeFragment sau
                return true;
            } else if (itemId == R.id.nav_admin_qa) {
                loadFragment(new SupportFragment());
                return true;
            } else if (itemId == R.id.nav_admin_notifications) {
                loadFragment(new NotificationFragment()); // Dùng Fragment để tránh văng app
                return true;
            } else if (itemId == R.id.nav_admin_profile) {
                loadFragment(new AdminUserFragment()); // MỤC 4: QUẢN LÝ USER
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