package com.example.doan_mau;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Sửa Button thành ImageButton
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_mau.model.Comment; // SỬA IMPORT

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();
    private EditText etComment;
    private ImageButton btnPostComment; // Sửa Button thành ImageButton
    private String questionId;
    private BlogApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        questionId = getIntent().getStringExtra("QUESTION_ID");
        api = RetrofitClient.getApi();

        recyclerView = findViewById(R.id.commentsRecyclerView);
        etComment = findViewById(R.id.etComment);
        btnPostComment = findViewById(R.id.btnPostComment);

        setupRecyclerView();
        loadComments();

        btnPostComment.setOnClickListener(v -> postComment());
    }

    private void setupRecyclerView() {
        adapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadComments() {
        // Logic để tải comment sẽ được sửa sau
    }

    private void postComment() {
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) return;

        String userId = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE).getString("USER_ID", "");

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("text", commentText);

        api.postComment(questionId, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CommentsActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    loadComments();
                } else {
                    Toast.makeText(CommentsActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CommentsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}