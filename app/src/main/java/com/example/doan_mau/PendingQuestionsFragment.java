package com.example.doan_mau;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_mau.model.BlogPost; // SỬA IMPORT

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingQuestionsFragment extends Fragment {

    private RecyclerView rvAdminList;
    private TextView tvEmpty;
    private BlogAdapter adapter;
    private List<BlogPost> pendingList = new ArrayList<>();
    private BlogApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_list, container, false);

        rvAdminList = view.findViewById(R.id.rvAdminList);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        api = RetrofitClient.getApi();

        setupRecyclerView();
        loadPendingQuestions();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new BlogAdapter(pendingList, true);
        rvAdminList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdminList.setAdapter(adapter);
    }

    private void loadPendingQuestions() {
        api.getPendingPosts().enqueue(new Callback<List<BlogPost>>() {
            @Override
            public void onResponse(Call<List<BlogPost>> call, Response<List<BlogPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pendingList.clear();
                    pendingList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(pendingList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<BlogPost>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}