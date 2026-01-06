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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private FloatingActionButton fabAI;
    private SearchView searchUser;
    private RecyclerView rvConversations;
    private ConversationAdapter adapter;
    private List<Map<String, Object>> conversationList;
    private BlogApi api;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        fabAI = view.findViewById(R.id.fabAI);
        searchUser = view.findViewById(R.id.searchUser);
        rvConversations = view.findViewById(R.id.rvChatHistory);
        
        setupApi();

        fabAI.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AiChatActivity.class);
            startActivity(intent);
        });

        setupSearch();
        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversations();
    }

    private void setupApi() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", "");
        currentUserId = prefs.getString("USER_ID", "");
        if (!token.isEmpty()) {
            api = RetrofitClient.getApiWithToken(token);
        } else {
            api = RetrofitClient.getApi();
        }
    }

    private void setupRecyclerView() {
        conversationList = new ArrayList<>();
        adapter = new ConversationAdapter(conversationList, (userId, userName) -> {
            Intent intent = new Intent(getActivity(), UserChatActivity.class);
            intent.putExtra("RECEIVER_ID", userId);
            intent.putExtra("RECEIVER_NAME", userName);
            startActivity(intent);
        });
        rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConversations.setAdapter(adapter);
    }

    private void loadConversations() {
        if (api == null || currentUserId.isEmpty()) return;

        api.getConversationList(currentUserId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) response.body().get("conversations");
                    if (list != null) {
                        conversationList.clear();
                        conversationList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("ChatFragment", "Load conversations failed: " + t.getMessage());
            }
        });
    }

    private void setupSearch() {
        searchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() >= 3) {
                    performSearch(query.trim());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void performSearch(String mssv) {
        api.searchUserByMssv(mssv).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> userData = (Map<String, Object>) response.body().get("user");
                    if (userData != null) {
                        String id = (String) userData.get("_id");
                        String name = (String) userData.get("hoTen");
                        
                        Intent intent = new Intent(getActivity(), UserChatActivity.class);
                        intent.putExtra("RECEIVER_ID", id);
                        intent.putExtra("RECEIVER_NAME", name);
                        startActivity(intent);
                    }
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Không tìm thấy MSSV này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}