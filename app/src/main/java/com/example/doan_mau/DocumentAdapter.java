package com.example.doan_mau;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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
        holder.tvTitle.setText(doc.title);
        holder.tvUploader.setText("Đăng bởi: " + doc.uploader);

        holder.btnView.setOnClickListener(v -> {
            // Link file từ server Node.js
            String fullUrl = "http://10.0.2.2:4000/" + doc.fileUrl;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return mList.size(); }

    public static class DocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUploader;
        Button btnView;
        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDocTitle);
            tvUploader = itemView.findViewById(R.id.tvDocUploader);
            btnView = itemView.findViewById(R.id.btnViewFile);
        }
    }
}