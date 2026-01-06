package com.example.doan_mau;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private EditText etCommentInput;
    private ImageButton btnSendComment;
    private CommentAdapter adapter;
    private List<BlogPost.CommentObj> commentList;
    private String questionId;
    private BlogApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        questionId = getIntent().getStringExtra("QUESTION_ID");
        if (questionId == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarComments);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvComments = findViewById(R.id.rvComments);
        etCommentInput = findViewById(R.id.etCommentInput);
        btnSendComment = findViewById(R.id.btnSendComment);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        api = RetrofitClient.getApi();

        loadComments();

        btnSendComment.setOnClickListener(v -> postComment());
    }

    private void loadComments() {
        // Fetch public posts to get updated comments for this specific question
        api.getPublicPosts().enqueue(new Callback<List<BlogPost>>() {
            @Override
            public void onResponse(Call<List<BlogPost>> call, Response<List<BlogPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (BlogPost post : response.body()) {
                        if (post._id.equals(questionId)) {
                            commentList.clear();
                            if (post.comments != null) {
                                commentList.addAll(post.comments);
                            }
                            adapter.notifyDataSetChanged();
                            if (!commentList.isEmpty()) {
                                rvComments.scrollToPosition(commentList.size() - 1);
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BlogPost>> call, Throwable t) {
                Toast.makeText(CommentsActivity.this, "Lỗi tải bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment() {
        String text = etCommentInput.getText().toString().trim();
        if (text.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("text", text);

        api.postComment(questionId, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    etCommentInput.setText("");
                    loadComments(); // Refresh list
                    Toast.makeText(CommentsActivity.this, "Đã gửi câu trả lời", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentsActivity.this, "Lỗi gửi bình luận", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CommentsActivity.this, "Mất kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}