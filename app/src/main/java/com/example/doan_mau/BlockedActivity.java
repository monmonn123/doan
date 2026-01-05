package com.example.doan_mau;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BlockedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked); //

        Button btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            // Đưa về Home lần nữa để an toàn
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Vô hiệu hóa nút quay lại
        Toast.makeText(this, "Hãy tập trung học bài!", Toast.LENGTH_SHORT).show();
    }
}