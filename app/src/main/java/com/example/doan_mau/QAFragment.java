package com.example.doan_mau;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_mau.model.BlogPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QAFragment extends Fragment {

    private RecyclerView rvQuestions;
    private FloatingActionButton fabAddQuestion;
    private BlogAdapter adapter;
    private List<BlogPost> publicQuestions = new ArrayList<>();
    private BlogApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa, container, false);

        rvQuestions = view.findViewById(R.id.rvQuestions);
        fabAddQuestion = view.findViewById(R.id.fabAddQuestion);

        // Luôn khởi tạo API
        api = RetrofitClient.getApi();

        setupRecyclerView();
        loadPublicQuestions();

        // Khôi phục chức năng đăng bài cho mọi người dùng
        fabAddQuestion.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CreatePostActivity.class));
        });

        return view;
    }

    private void setupRecyclerView() {
        // Luôn khởi tạo adapter với danh sách trống và cờ user
        adapter = new BlogAdapter(publicQuestions, false);
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuestions.setAdapter(adapter);
    }

    private void loadPublicQuestions() {
        // Gọi API để lấy danh sách bài đăng
        api.getPublicPosts().enqueue(new Callback<List<BlogPost>>() {
            @Override
            public void onResponse(Call<List<BlogPost>> call, Response<List<BlogPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    publicQuestions.clear();
                    publicQuestions.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Cập nhật giao diện
                }
            }

            @Override
            public void onFailure(Call<List<BlogPost>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu Hỏi & Đáp", Toast.LENGTH_SHORT).show();
            }
        });
    }
}