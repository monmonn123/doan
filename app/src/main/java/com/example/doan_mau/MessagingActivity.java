package com.example.doan_mau;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MessagingActivity extends AppCompatActivity {

    private EditText etSearchUser;
    private FloatingActionButton fabAiBot;
    private RecyclerView rvConversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        etSearchUser = findViewById(R.id.etSearchUser);
        fabAiBot = findViewById(R.id.fabAiBot);
        rvConversations = findViewById(R.id.rvConversations);

        rvConversations.setLayoutManager(new LinearLayoutManager(this));

        fabAiBot.setOnClickListener(v -> {
            Toast.makeText(this, "Đang kết nối với AI Bot...", Toast.LENGTH_SHORT).show();
        });

        etSearchUser.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearchUser.getText().toString();
            Toast.makeText(MessagingActivity.this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}
