package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnLogout = findViewById(R.id.btnLogoutAdmin);
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });
    }
}