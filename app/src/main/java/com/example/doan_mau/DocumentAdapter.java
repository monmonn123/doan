package com.example.doan_mau;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_mau.model.DocumentModel; // SỬA IMPORT

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {
    private List<DocumentModel> mList;
    private Context mContext;

    public DocumentAdapter(Context context, List<DocumentModel> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_document, parent, false);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder holder, int position) {
        DocumentModel doc = mList.get(position);
        holder.tvTitle.setText(doc.getTitle());
        holder.tvUploader.setText("Đăng bởi: " + doc.getUploaderName());

        holder.btnView.setOnClickListener(v -> {
            String fullUrl = "http://10.0.2.2:4000/" + doc.getFilePath();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
            mContext.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(mContext)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa tài liệu này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteDocument(doc, position))
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void deleteDocument(DocumentModel doc, int position) {
        BlogApi apiService = RetrofitClient.getApi();
        apiService.deleteDocument(doc.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(mContext, "Đã xóa tài liệu", Toast.LENGTH_SHORT).show();
                    mList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size());
                } else {
                    Toast.makeText(mContext, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return mList.size(); }

    public static class DocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUploader;
        Button btnView;
        ImageButton btnDelete;

        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDocTitle);
            tvUploader = itemView.findViewById(R.id.tvDocUploader);
            btnView = itemView.findViewById(R.id.btnViewFile);
            btnDelete = itemView.findViewById(R.id.btnDeleteDoc);
        }
    }
}