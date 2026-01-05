package com.example.doan_mau;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private ImageView imgSearch, imgNotifications;

    private final String CHANNEL_ID = "ute_channel";

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        imgSearch = findViewById(R.id.imgSearch);
        imgNotifications = findViewById(R.id.imgNotifications);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<StudentData.Category> dataList = StudentData.getMockData();
        adapter = new CategoryAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        imgSearch.setOnClickListener(v -> Toast.makeText(this, "Chức năng tìm kiếm!", Toast.LENGTH_SHORT).show());
        imgNotifications.setOnClickListener(v -> {
            sendNotification();
            Toast.makeText(this, "Đã gửi thông báo nhắc nhở!", Toast.LENGTH_SHORT).show();
        });

        fab.setOnClickListener(v -> Toast.makeText(this, "Quét mã QR!", Toast.LENGTH_SHORT).show());

        // SỬA LỖI: Thêm onNavigationItemReselected để tránh crash
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            // Không làm gì cả để tránh load lại màn hình một cách không cần thiết
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Đang ở trang chủ rồi nên không làm gì
                return true;
            } else if (itemId == R.id.nav_docs) {
                Toast.makeText(MainActivity.this, "Mở kho tài liệu!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_chat) {
                Toast.makeText(MainActivity.this, "Mở Chat AI!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Kênh thông báo MyUTE";
            String description = "Kênh dùng để gửi các thông báo của ứng dụng MyUTE";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("MyUTE nhắc nhở")
                .setContentText("Đã đến giờ học Lập trình Web! Vào lớp thôi!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}
