package com.example.doan_mau;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QAFragment extends Fragment {

    private RecyclerView rvQuestions;
    private BlogAdapter adapter;
    private List<BlogPost> publicQuestions = new ArrayList<>();
    private FloatingActionButton fabAddQuestion;
    private BlogApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa, container, false);
        rvQuestions = view.findViewById(R.id.rvQuestions);
        fabAddQuestion = view.findViewById(R.id.fabAddQuestion);
        setupRecyclerView();
        fabAddQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPublicQuestions();
    }

    private void setupRecyclerView() {
        adapter = new BlogAdapter(publicQuestions, false);
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuestions.setAdapter(adapter);
    }

    private void setupApi() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", "");
        if (!token.isEmpty()) {
            api = RetrofitClient.getApiWithToken(token);
        } else {
            api = RetrofitClient.getApi();
        }
    }

    private void loadPublicQuestions() {
        setupApi();
        api.getPublicPosts().enqueue(new Callback<List<BlogPost>>() {
            @Override
            public void onResponse(Call<List<BlogPost>> call, Response<List<BlogPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    publicQuestions.clear();
                    publicQuestions.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Code: " + response.code());
                    Toast.makeText(getContext(), "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BlogPost>> call, Throwable t) {
                Log.e("NETWORK_ERROR", t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}