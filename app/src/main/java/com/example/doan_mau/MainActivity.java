package com.example.doan_mau;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            // Tránh load lại màn hình
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_docs) {
                Toast.makeText(MainActivity.this, "Mở kho tài liệu!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_support) {
                showSupportDialog();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void showSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_support_options, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnFaq = dialogView.findViewById(R.id.btnFaq);
        Button btnMessage = dialogView.findViewById(R.id.btnMessage);

        btnFaq.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FaqActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        btnMessage.setOnClickListener(v -> {
            // MỞ MÀN HÌNH NHẮN TIN
            Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
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
