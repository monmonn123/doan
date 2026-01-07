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
import java.util.Collections;
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

        // Lấy danh sách App chuẩn chỉnh (đã sửa logic lọc)
        List<ApplicationInfo> installedApps = getInstalledApps();
        Set<String> selectedApps = getSelectedAppsFromPrefs();

        adapter = new AppAdapter(this, installedApps, selectedApps);
        recyclerView.setAdapter(adapter);

        btnStartBlocking.setOnClickListener(v -> startBlockingSession());

        btnActivateService.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            Toast.makeText(this, "Hãy tìm và bật 'MyUTE Blocker' nhé!", Toast.LENGTH_LONG).show();
            startActivity(intent);
        });
    }

    // --- HÀM QUAN TRỌNG: LỌC APP CHUẨN ---
    private List<ApplicationInfo> getInstalledApps() {
        PackageManager pm = getPackageManager();
        // Lấy tất cả, không lọc cờ hệ thống nữa
        List<ApplicationInfo> allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> showableApps = new ArrayList<>();

        for (ApplicationInfo app : allApps) {
            // Chỉ lấy những App có icon mở được từ màn hình chính (bao gồm cả YouTube, FB...)
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                showableApps.add(app);
            }
        }

        // Sắp xếp A-Z
        Collections.sort(showableApps, (app1, app2) ->
                app1.loadLabel(pm).toString().compareToIgnoreCase(app2.loadLabel(pm).toString())
        );

        return showableApps;
    }

    private void startBlockingSession() {
        String durationStr = edtBlockDuration.getText().toString();
        if (TextUtils.isEmpty(durationStr)) {
            Toast.makeText(this, "Vui lòng nhập số phút cần chặn", Toast.LENGTH_SHORT).show();
            return;
        }

        int durationMinutes = Integer.parseInt(durationStr);
        if (durationMinutes <= 0) {
            Toast.makeText(this, "Thời gian phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<String> selectedApps = adapter.getSelectedApps();
        if (selectedApps.isEmpty()) {
            Toast.makeText(this, "Chọn ít nhất 1 app để chặn nhé", Toast.LENGTH_SHORT).show();
            return;
        }

        long currentTime = System.currentTimeMillis();
        long blockUntil = currentTime + TimeUnit.MINUTES.toMillis(durationMinutes);

        SharedPreferences prefs = getSharedPreferences("AppBlocker", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("blocked_apps", selectedApps);
        editor.putLong("block_until", blockUntil);
        editor.apply();

        Toast.makeText(this, "Đã bật chế độ Tập trung trong " + durationMinutes + " phút!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private Set<String> getSelectedAppsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppBlocker", MODE_PRIVATE);
        return prefs.getStringSet("blocked_apps", new HashSet<>());
    }
}