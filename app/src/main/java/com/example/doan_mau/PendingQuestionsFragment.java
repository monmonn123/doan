package com.example.doan_mau;

import android.app.AlertDialog;
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
    private PendingQuestionAdapter adapter;
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
        adapter = new PendingQuestionAdapter(pendingList, new PendingQuestionAdapter.OnActionClickListener() {
            @Override
            public void onApprove(String id) {
                approveQuestion(id);
            }

            @Override
            public void onReject(String id) {
                showRejectDialog(id);
            }
        });
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
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveQuestion(String id) {
        Map<String, String> body = new HashMap<>();
        body.put("action", "approved");
        api.updateStatus(id, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã duyệt bài thành công!", Toast.LENGTH_SHORT).show();
                    loadPendingQuestions();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRejectDialog(String id) {
        String[] reasons = {
            "Nội dung không phù hợp",
            "Ngôn từ gây thù ghét",
            "Spam / Quảng cáo",
            "Sai chủ đề",
            "Câu hỏi đã tồn tại"
        };
        boolean[] checkedItems = new boolean[reasons.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lý do từ chối bài viết");
        builder.setMultiChoiceItems(reasons, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("Từ chối bài", (dialog, which) -> {
            StringBuilder finalReason = new StringBuilder();
            for (int i = 0; i < reasons.length; i++) {
                if (checkedItems[i]) {
                    if (finalReason.length() > 0) finalReason.append(", ");
                    finalReason.append(reasons[i]);
                }
            }
            if (finalReason.length() == 0) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một lý do", Toast.LENGTH_SHORT).show();
                return;
            }
            rejectQuestion(id, finalReason.toString());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void rejectQuestion(String id, String reason) {
        Map<String, String> body = new HashMap<>();
        body.put("action", "rejected");
        body.put("message", reason);
        api.updateStatus(id, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã từ chối bài viết!", Toast.LENGTH_SHORT).show();
                    loadPendingQuestions();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}