package com.example.doan_mau;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogMainActivity extends AppCompatActivity {
    RecyclerView rvBlog;
    BlogAdapter adapter;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_main);

        rvBlog = findViewById(R.id.rvBlog);
        fabAdd = findViewById(R.id.fabAddPost);
        rvBlog.setLayoutManager(new LinearLayoutManager(this));

        // 1. Lấy dữ liệu từ Server MongoDB Port 4000
        loadNewsfeed();

        // 2. Bấm nút + để đi tới trang Đăng bài
        fabAdd.setOnClickListener(v -> {
            // startActivity(new Intent(this, CreatePostActivity.class));
        });
    }

    private void loadNewsfeed() {
        RetrofitClient.getApi().getPublicPosts().enqueue(new Callback<List<BlogPost>>() {
            @Override
            public void onResponse(Call<List<BlogPost>> call, Response<List<BlogPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Hiển thị danh sách bài đã duyệt
                    adapter = new BlogAdapter(response.body(), false); // false = SV lướt tin
                    rvBlog.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<BlogPost>> call, Throwable t) {
                Toast.makeText(BlogMainActivity.this, "Không kết nối được Server Port 4000!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}