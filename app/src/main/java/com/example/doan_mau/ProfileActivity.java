package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
// import com.bumptech.glide.Glide; // Tạm thời chú thích
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView imgProfileAvatar;
    private EditText etFullName, etMssv, etEmail;
    private Button btnUpdateProfile, btnLogout;
    private BlogApi api;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        etFullName = findViewById(R.id.etProfileFullName);
        etMssv = findViewById(R.id.etProfileMssv);
        etEmail = findViewById(R.id.etProfileEmail);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        userId = prefs.getString("USER_ID", "");
        String token = prefs.getString("USER_TOKEN", "");
        api = RetrofitClient.getApiWithToken(token);

        loadUserProfile();

        btnUpdateProfile.setOnClickListener(v -> updateUserProfile());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserProfile() {
        if (userId.isEmpty() || api == null) return;

        api.getUserProfile(userId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null && (Boolean) response.body().get("success")) {
                    Map<String, Object> user = (Map<String, Object>) response.body().get("user");
                    if (user != null) {
                        etFullName.setText((String) user.get("hoTen"));
                        etMssv.setText((String) user.get("mssv"));
                        etEmail.setText((String) user.get("email"));

                        String avatarPath = (String) user.get("avatar");
                        // Tạm thời chú thích phần Glide
                        // if (avatarPath != null && !avatarPath.isEmpty()) {
                        //     Glide.with(ProfileActivity.this)
                        //          .load("http://10.0.2.2:4000/" + avatarPath.replace("\\", "/"))
                        //          .into(imgProfileAvatar);
                        // }
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("ProfileActivity", "Lỗi tải thông tin người dùng", t);
            }
        });
    }

    private void updateUserProfile() {
        String hoTen = etFullName.getText().toString();
        String mssv = etMssv.getText().toString();
        String email = etEmail.getText().toString();

        Map<String, String> body = new HashMap<>();
        body.put("hoTen", hoTen);
        body.put("mssv", mssv);
        body.put("email", email);

        api.updateUserProfile(userId, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null && (Boolean) response.body().get("success")) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại thông tin trong SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE).edit();
                    editor.putString("FULL_NAME", hoTen);
                    editor.putString("USER_MSSV", mssv);
                    editor.putString("USER_EMAIL", email);
                    editor.apply();
                } else {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}