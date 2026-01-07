package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {
    private EditText edtTitle;
    private TextView tvFileName;
    private Button btnSelect, btnConfirm;
    private Uri selectedFileUri;

    private final ActivityResultLauncher<Intent> filePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    tvFileName.setText("Đã chọn: " + selectedFileUri.getLastPathSegment());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        edtTitle = findViewById(R.id.edtFileTitle);
        tvFileName = findViewById(R.id.tvFileName);
        btnSelect = findViewById(R.id.btnSelectFile);
        btnConfirm = findViewById(R.id.btnConfirmUpload);

        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePicker.launch(intent);
        });

        btnConfirm.setOnClickListener(v -> uploadFileToServer());
    }

    private void uploadFileToServer() {
        if (selectedFileUri == null || edtTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và chọn file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy tên người dùng từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String uploaderName = prefs.getString("FULL_NAME", "Người dùng ẩn danh");

        try {
            File file = uriToFile(selectedFileUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedFileUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            RequestBody title = RequestBody.create(MultipartBody.FORM, edtTitle.getText().toString());
            RequestBody uploader = RequestBody.create(MultipartBody.FORM, uploaderName);

            // Sử dụng RetrofitClient đã được cấu hình sẵn
            BlogApi apiService = RetrofitClient.getApiWithToken(prefs.getString("USER_TOKEN", ""));
            apiService.uploadFile(title, uploader, body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UploadActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UploadActivity.this, DocumentListActivity.class));
                        finish();
                    } else {
                        Toast.makeText(UploadActivity.this, "Upload thất bại, code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(UploadActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private File uriToFile(Uri uri) throws Exception {
        InputStream is = getContentResolver().openInputStream(uri);
        File tempFile = new File(getCacheDir(), "temp_file");
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) fos.write(buffer, 0, read);
        fos.close();
        is.close();
        return tempFile;
    }
}