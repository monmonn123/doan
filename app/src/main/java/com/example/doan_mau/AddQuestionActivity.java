package com.example.doan_mau;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuestionActivity extends AppCompatActivity {

    private EditText etQuestionContent;
    private Button btnSubmitQuestion;
    private BlogApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        Toolbar toolbar = findViewById(R.id.toolbarAddQuestion);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etQuestionContent = findViewById(R.id.etQuestionContent);
        btnSubmitQuestion = findViewById(R.id.btnSubmitQuestion);

        api = RetrofitClient.getApi();

        btnSubmitQuestion.setOnClickListener(v -> submitQuestion());
    }

    private void submitQuestion() {
        String content = etQuestionContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody rbUserId = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody rbContent = RequestBody.create(MediaType.parse("text/plain"), content);

        // Tạo một Part trống cho ảnh nếu không có
        MultipartBody.Part imagePart = null;

        api.postQuestion(rbUserId, rbContent, imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddQuestionActivity.this, "Gửi câu hỏi thành công! Đang chờ duyệt.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddQuestionActivity.this, "Lỗi gửi câu hỏi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddQuestionActivity.this, "Mất kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}