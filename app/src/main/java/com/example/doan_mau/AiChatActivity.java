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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiChatActivity extends AppCompatActivity {

    private RecyclerView rvAiChat;
    private EditText etAiMessage;
    private ImageButton btnSendAi;
    private AiChatAdapter adapter;
    private List<AiChatMessage> messageList;
    private BlogApi api;
    private String conversationId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        Toolbar toolbar = findViewById(R.id.toolbarAiChat);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvAiChat = findViewById(R.id.rvAiChat);
        etAiMessage = findViewById(R.id.etAiMessage);
        btnSendAi = findViewById(R.id.btnSendAi);

        messageList = new ArrayList<>();
        adapter = new AiChatAdapter(messageList);
        rvAiChat.setLayoutManager(new LinearLayoutManager(this));
        rvAiChat.setAdapter(adapter);

        setupRetrofit();

        btnSendAi.setOnClickListener(v -> sendMessage());
        
        messageList.add(new AiChatMessage("Xin chào! Tôi là AI Assistant. Tôi có thể giúp gì cho bạn hôm nay?", false));
        adapter.notifyDataSetChanged();
    }

    private void setupRetrofit() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", "");
        
        Log.d("AiChat", "Using Token: " + token);

        if (token.isEmpty()) {
            Toast.makeText(this, "Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn", Toast.LENGTH_LONG).show();
            return;
        }

        api = RetrofitClient.getApiWithToken(token);
    }

    private void sendMessage() {
        String content = etAiMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        messageList.add(new AiChatMessage(content, true));
        adapter.notifyItemInserted(messageList.size() - 1);
        rvAiChat.scrollToPosition(messageList.size() - 1);
        etAiMessage.setText("");

        Map<String, String> body = new HashMap<>();
        body.put("message", content);
        if (conversationId != null) {
            body.put("conversationId", conversationId);
        }

        api.sendAiMessage(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    if (data != null) {
                        String aiText = (String) data.get("response");
                        conversationId = (String) data.get("conversationId");

                        messageList.add(new AiChatMessage(aiText, false));
                        adapter.notifyItemInserted(messageList.size() - 1);
                        rvAiChat.scrollToPosition(messageList.size() - 1);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jObjError = new JSONObject(errorBody);
                        String msg = jObjError.optString("message", "Lỗi xác thực");
                        Log.e("AiChat", "Server Error: " + errorBody);
                        Toast.makeText(AiChatActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(AiChatActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Log.e("AiChat", "Failure: " + t.getMessage());
                Toast.makeText(AiChatActivity.this, "Không thể kết nối Backend", Toast.LENGTH_SHORT).show();
            }
        });
    }
}