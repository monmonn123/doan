package com.example.doan_mau;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DocumentListActivity extends AppCompatActivity {
    private RecyclerView rvDocuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        rvDocuments = findViewById(R.id.rvDocuments);
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));

        // GỌI HÀM NÀY ĐỂ HIỆN SẴN DỮ LIỆU
        loadDocuments();
    }
    private void loadDocuments() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:4000/") // Địa chỉ máy ảo
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getDocuments().enqueue(new Callback<List<DocumentModel>>() {
            @Override
            public void onResponse(Call<List<DocumentModel>> call, Response<List<DocumentModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DocumentModel> docs = response.body();

                    // ĐÂY LÀ DÒNG QUAN TRỌNG NHẤT:
                    DocumentAdapter adapter = new DocumentAdapter(DocumentListActivity.this, docs);
                    rvDocuments.setAdapter(adapter); // Gắn adapter vào RecyclerView

                    Toast.makeText(DocumentListActivity.this, "Đã thấy " + docs.size() + " tài liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DocumentModel>> call, Throwable t) {
                Toast.makeText(DocumentListActivity.this, "Lỗi tải danh sách!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}