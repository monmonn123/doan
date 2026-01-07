package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvWelcomeName, tvMssvInfo;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvMssvInfo = findViewById(R.id.tvMssvInfo);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Hiển thị thông tin người dùng từ database
        displayUserData();

        // Hiển thị danh sách chức năng
        setupFunctionList();

        // Xử lý Bottom Navigation
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                } else if (itemId == R.id.nav_home) {
                    return true;
                }
                return false;
            });
        }
    }

    private void displayUserData() {
        SharedPreferences currentUserPrefs = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        String email = currentUserPrefs.getString("email", null);

        if (email != null) {
            SharedPreferences userAccountsPrefs = getSharedPreferences("USER_ACCOUNTS", MODE_PRIVATE);
            String fullName = userAccountsPrefs.getString(email + "_fullName", "Sinh viên");
            String mssv = userAccountsPrefs.getString(email + "_mssv", "Chưa có MSSV");

            tvWelcomeName.setText("Xin chào, " + fullName);
            tvMssvInfo.setText("MSSV: " + mssv);
        }
    }

    private void setupFunctionList() {
        List<StudentData.Category> categoryList = StudentData.getMockData();
        adapter = new CategoryAdapter(this, categoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại dữ liệu khi quay về từ trang Profile
        displayUserData();
    }
}