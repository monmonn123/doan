package com.example.doan_mau;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan_mau.model.AiChatMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserChatActivity extends AppCompatActivity {

    private RecyclerView rvUserChat;
    private EditText etUserMessage;
    private ImageButton btnSendUser;
    private AiChatAdapter adapter; // Dùng lại adapter chat cho đồng bộ giao diện
    private List<AiChatMessage> messageList;
    private BlogApi api;
    private String currentUserId;
    private String receiverId;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");

        Toolbar toolbar = findViewById(R.id.toolbarUserChat);
        setSupportActionBar(toolbar);
        toolbar.setTitle(receiverName);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvUserChat = findViewById(R.id.rvUserChat);
        etUserMessage = findViewById(R.id.etUserMessage);
        btnSendUser = findViewById(R.id.btnSendUser);

        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", "");
        String token = prefs.getString("USER_TOKEN", "");

        api = RetrofitClient.getApiWithToken(token);

        messageList = new ArrayList<>();
        adapter = new AiChatAdapter(messageList);
        rvUserChat.setLayoutManager(new LinearLayoutManager(this));
        rvUserChat.setAdapter(adapter);

        loadChatHistory();

        btnSendUser.setOnClickListener(v -> sendMessage());
    }

    private void loadChatHistory() {
        api.getUserChatHistory(currentUserId, receiverId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> messages = (List<Map<String, Object>>) response.body().get("messages");
                    if (messages != null) {
                        for (Map<String, Object> msg : messages) {
                            String senderId = (String) msg.get("senderId");
                            String content = (String) msg.get("content");
                            messageList.add(new AiChatMessage(content, senderId.equals(currentUserId)));
                        }
                        adapter.notifyDataSetChanged();
                        rvUserChat.scrollToPosition(messageList.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("UserChat", "Load history failed: " + t.getMessage());
            }
        });
    }

    private void sendMessage() {
        String content = etUserMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Map<String, String> body = new HashMap<>();
        body.put("senderId", currentUserId);
        body.put("receiverId", receiverId);
        body.put("content", content);

        api.sendUserMessage(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    messageList.add(new AiChatMessage(content, true));
                    adapter.notifyItemInserted(messageList.size() - 1);
                    rvUserChat.scrollToPosition(messageList.size() - 1);
                    etUserMessage.setText("");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(UserChatActivity.this, "Không gửi được tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}