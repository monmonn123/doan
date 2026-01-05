package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcomeName, tvMssvInfo;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Đảm bảo layout là activity_main.xml

        // 1. ÁNH XẠ (Kết nối với giao diện)
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvMssvInfo = findViewById(R.id.tvMssvInfo);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);

        // 2. HIỂN THỊ TÊN VÀ MSSV (Lấy từ Login)
        setupUserInfo();

        // 3. HIỂN THỊ DANH SÁCH CHỨC NĂNG (Dùng StudentData và CategoryAdapter)
        setupFunctionList();

        // 4. XỬ LÝ THANH ĐIỀU HƯỚNG BÊN DƯỚI
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    // Mở trang cá nhân
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    Toast.makeText(this, "Chức năng Chat đang phát triển", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupUserInfo() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

        if (!isLoggedIn) {
            // Chưa đăng nhập thì đá về Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String fullName = prefs.getString("FULL_NAME", "Sinh viên");
        String mssv = prefs.getString("MSSV", "---");

        if (tvWelcomeName != null) tvWelcomeName.setText("Xin chào, " + fullName);
        if (tvMssvInfo != null) tvMssvInfo.setText("MSSV: " + mssv);
    }

    private void setupFunctionList() {
        // Lấy dữ liệu từ file StudentData đã sửa icon
        List<StudentData.Category> categoryList = StudentData.getMockData();

        // Đổ dữ liệu vào Adapter cũ của bạn
        adapter = new CategoryAdapter(this, categoryList);

        // Hiển thị lên RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}