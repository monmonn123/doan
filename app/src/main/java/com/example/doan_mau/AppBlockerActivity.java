package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AppBlockerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppAdapter adapter;
    private Button btnStartBlocking, btnActivateService;
    private EditText edtBlockDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_blocker);

        recyclerView = findViewById(R.id.appsRecyclerView);
        btnStartBlocking = findViewById(R.id.btnStartBlocking);
        btnActivateService = findViewById(R.id.btnActivateService);
        edtBlockDuration = findViewById(R.id.edtBlockDuration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Set<String> selectedApps = getSelectedAppsFromPrefs();
        List<ApplicationInfo> installedApps = getInstalledApps();
        adapter = new AppAdapter(this, installedApps, selectedApps);
        recyclerView.setAdapter(adapter);

        btnStartBlocking.setOnClickListener(v -> startBlockingSession());

        btnActivateService.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            Toast.makeText(this, "Vui lòng tìm và bật dịch vụ MyUTE Blocker", Toast.LENGTH_LONG).show();
            startActivity(intent);
        });
    }

    private void startBlockingSession() {
        String durationStr = edtBlockDuration.getText().toString();
        if (TextUtils.isEmpty(durationStr)) {
            Toast.makeText(this, "Vui lòng nhập số phút cần chặn", Toast.LENGTH_SHORT).show();
            return;
        }

        int durationMinutes = Integer.parseInt(durationStr);
        if (durationMinutes <= 0) {
            Toast.makeText(this, "Thời gian chặn phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<String> selectedApps = adapter.getSelectedApps();
        if (selectedApps.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ứng dụng để chặn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tính toán thời gian kết thúc
        long currentTime = System.currentTimeMillis();
        long blockUntil = currentTime + TimeUnit.MINUTES.toMillis(durationMinutes);

        // Lưu thông tin phiên chặn
        SharedPreferences prefs = getSharedPreferences("AppBlocker", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("blocked_apps", selectedApps);
        editor.putLong("block_until", blockUntil);
        editor.apply();

        Toast.makeText(this, "Đã bắt đầu phiên tập trung!", Toast.LENGTH_SHORT).show();
        finish(); // Đóng màn hình và quay lại
    }

    private List<ApplicationInfo> getInstalledApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> userApps = new ArrayList<>();
        for (ApplicationInfo app : allApps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userApps.add(app);
            }
        }
        return userApps;
    }

    private Set<String> getSelectedAppsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppBlocker", MODE_PRIVATE);
        return prefs.getStringSet("blocked_apps", new HashSet<>());
    }
}
