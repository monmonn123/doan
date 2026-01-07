package com.example.doan_mau;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {
    private String targetUserId;
    private String currentUserId;
    private boolean isReported = false;

    private ImageView ivAvatar;
    private TextView tvName, tvMssv, tvRole, tvQuestionsCount, tvChatsCount;
    private Button btnReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Get current user info
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", "");

        // Get target user ID from intent
        targetUserId = getIntent().getStringExtra("USER_ID");
        if (targetUserId == null || targetUserId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadUserProfile();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.ivUserAvatar);
        tvName = findViewById(R.id.tvUserName);
        tvMssv = findViewById(R.id.tvUserMssv);
        tvRole = findViewById(R.id.tvUserRole);
        tvQuestionsCount = findViewById(R.id.tvQuestionsCount);
        tvChatsCount = findViewById(R.id.tvChatsCount);
        btnReport = findViewById(R.id.btnReportUser);

        // Disable report button if viewing own profile
        if (targetUserId.equals(currentUserId)) {
            btnReport.setVisibility(View.GONE);
        }

        btnReport.setOnClickListener(v -> showReportDialog());
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", "");

        RetrofitClient.getApiWithToken(token).getUserProfile(targetUserId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> data = (Map<String, Object>) response.body().get("data");
                        if (data != null) {
                            Map<String, Object> user = (Map<String, Object>) data.get("user");
                            Map<String, Object> stats = (Map<String, Object>) data.get("stats");
                            Boolean reported = (Boolean) data.get("isReported");

                            if (user != null) {
                                String hoTen = (String) user.get("hoTen");
                                String mssv = (String) user.get("mssv");
                                String avatar = (String) user.get("avatar");
                                String role = (String) user.get("role");

                                tvName.setText(hoTen != null ? hoTen : "Ẩn danh");
                                tvMssv.setText(mssv != null ? mssv : "N/A");

                                // Format role
                                if (role != null) {
                                    switch (role) {
                                        case "admin":
                                            tvRole.setText("Quản trị viên");
                                            tvRole.setBackgroundResource(R.drawable.bg_user_message);
                                            break;
                                        case "manager":
                                            tvRole.setText("Quản lý");
                                            break;
                                        default:
                                            tvRole.setText("Sinh viên");
                                    }
                                }

                                // Load avatar
                                if (avatar != null && !avatar.isEmpty()) {
                                    loadAvatarFromUrl("http://192.168.1.196:4000/" + avatar, ivAvatar);
                                }

                                // Load stats
                                if (stats != null) {
                                    int questions = stats.get("questionsAsked") != null ?
                                            ((Double) stats.get("questionsAsked")).intValue() : 0;
                                    int chats = stats.get("totalChats") != null ?
                                            ((Double) stats.get("totalChats")).intValue() : 0;

                                    tvQuestionsCount.setText(String.valueOf(questions));
                                    tvChatsCount.setText(String.valueOf(chats));
                                }

                                isReported = reported != null && reported;
                                updateReportButton();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(UserProfileActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReportButton() {
        if (isReported) {
            btnReport.setText("Đã báo cáo");
            btnReport.setEnabled(false);
        } else {
            btnReport.setText("Báo cáo người dùng");
            btnReport.setEnabled(true);
        }
    }

    private void showReportDialog() {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", "");

        // Load report reasons first
        RetrofitClient.getApiWithToken(token).getReportReasons().enqueue(new Callback<java.util.List<ReportReason>>() {
            @Override
            public void onResponse(Call<java.util.List<ReportReason>> call, Response<java.util.List<ReportReason>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showReportReasonDialog(response.body());
                } else {
                    // Use default reasons
                    String[] defaultReasons = {
                            "Spam / Quảng cáo",
                            "Quấy rối / Xúc phạm",
                            "Nội dung không phù hợp",
                            "Tài khoản giả mạo",
                            "Khác"
                    };
                    String[] reasonValues = {"spam", "harassment", "inappropriate_content", "fake_account", "other"};
                    showSimpleReportDialog(defaultReasons, reasonValues);
                }
            }

            @Override
            public void onFailure(Call<java.util.List<ReportReason>> call, Throwable t) {
                String[] defaultReasons = {
                        "Spam / Quảng cáo",
                        "Quấy rối / Xúc phạm",
                        "Nội dung không phù hợp",
                        "Tài khoản giả mạo",
                        "Khác"
                };
                String[] reasonValues = {"spam", "harassment", "inappropriate_content", "fake_account", "other"};
                showSimpleReportDialog(defaultReasons, reasonValues);
            }
        });
    }

    private void showReportReasonDialog(java.util.List<ReportReason> reasons) {
        String[] items = new String[reasons.size()];
        final String[] reasonValues = new String[reasons.size()];

        for (int i = 0; i < reasons.size(); i++) {
            items[i] = reasons.get(i).label;
            reasonValues[i] = reasons.get(i).value;
        }

        new AlertDialog.Builder(this)
                .setTitle("Lý do báo cáo")
                .setItems(items, (dialog, which) -> {
                    submitReport(reasonValues[which], "");
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showSimpleReportDialog(String[] labels, String[] values) {
        new AlertDialog.Builder(this)
                .setTitle("Lý do báo cáo")
                .setItems(labels, (dialog, which) -> {
                    submitReport(values[which], "");
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void submitReport(String reason, String description) {
        SharedPreferences prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("USER_TOKEN", "");

        Map<String, String> body = new HashMap<>();
        body.put("reportedUserId", targetUserId);
        body.put("reason", reason);
        body.put("description", description);

        RetrofitClient.getApiWithToken(token).createReport(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    String message = (String) response.body().get("message");

                    if (success != null && success) {
                        Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        isReported = true;
                        updateReportButton();
                    } else {
                        Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAvatarFromUrl(final String imageUrl, final ImageView imageView) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                imageView.post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                // Use default avatar on error
                imageView.post(() -> imageView.setImageResource(R.drawable.ic_person));
            }
        }).start();
    }
}

