package com.example.doan_mau;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserFragment extends Fragment {

    private RecyclerView rvAdminList;
    private TextView tvEmpty;
    private UserManageAdapter adapter;
    private List<Map<String, Object>> userList = new ArrayList<>();
    private BlogApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_list, container, false);
        
        rvAdminList = view.findViewById(R.id.rvAdminList);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        
        // Lấy Token để khởi tạo API có quyền truy cập
        setupApi();
        
        setupRecyclerView();
        loadUsers();
        
        return view;
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

    private void setupRecyclerView() {
        adapter = new UserManageAdapter(userList, (userId, newRole) -> updateRole(userId, newRole));
        rvAdminList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdminList.setAdapter(adapter);
    }

    private void loadUsers() {
        if (api == null) setupApi();
        
        api.getAllUsers().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> users = (List<Map<String, Object>>) response.body().get("users");
                    if (users != null) {
                        userList.clear();
                        userList.addAll(users);
                        adapter.notifyDataSetChanged();
                        tvEmpty.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } else {
                    Log.e("AdminUser", "Error: " + response.code());
                    Toast.makeText(getContext(), "Lỗi xác thực: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRole(String userId, String newRole) {
        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("newRole", newRole);

        api.updateUserRole(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã cập nhật quyền thành công", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Tải lại danh sách sau khi cập nhật
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}