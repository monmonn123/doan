package com.example.doan_mau;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan_mau.model.BlogPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BlogAdapter adapter;
    private List<BlogPost> postList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_main);

        recyclerView = findViewById(R.id.blogRecyclerView); // ID này đã đúng
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BlogAdapter(postList, false);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddPost);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(BlogMainActivity.this, CreatePostActivity.class));
        });

        loadPosts();
    }

    private void loadPosts() {
        RetrofitClient.getApi().getPublicPosts().enqueue(new Callback<List<BlogPost>>() {
            @Override
            public void onResponse(Call<List<BlogPost>> call, Response<List<BlogPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<BlogPost>> call, Throwable t) {
                // Handle failure
            }
        });
    }
}