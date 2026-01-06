package com.example.doan_mau;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView imgProfile;
    private EditText etFullName, etGmail, etPhoneNumber;
    private Button btnSaveChanges;
    private FloatingActionButton fabChangeAvatar;

    private Integer[] avatars = {
            R.drawable.bogo,
            R.drawable.lion,
            R.drawable.jult,
            R.drawable.mrbigrender,
            R.drawable.nick,
            R.drawable.images,
            R.drawable.bellwether,
            R.drawable.beaver,
            R.drawable.gary_zootopia_2
    };
    private int selectedAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfile = findViewById(R.id.imgProfile);
        etFullName = findViewById(R.id.etFullName);
        etGmail = findViewById(R.id.etGmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        fabChangeAvatar = findViewById(R.id.fabChangeAvatar);

        // Email là định danh, không cho phép chỉnh sửa
        etGmail.setEnabled(false);

        loadUserData();

        fabChangeAvatar.setOnClickListener(v -> showAvatarPickerDialog());

        btnSaveChanges.setOnClickListener(v -> {
            saveChanges();
        });
    }

    private void loadUserData() {
        SharedPreferences currentUserPrefs = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        String email = currentUserPrefs.getString("email", null);

        if (email != null) {
            SharedPreferences userAccountsPrefs = getSharedPreferences("USER_ACCOUNTS", MODE_PRIVATE);

            String fullName = userAccountsPrefs.getString(email + "_fullName", "Không có tên");
            String phoneNumber = userAccountsPrefs.getString(email + "_phoneNumber", ""); // Tải SĐT
            selectedAvatar = userAccountsPrefs.getInt(email + "_avatar", R.drawable.bogo); // Tải avatar

            etFullName.setText(fullName);
            etGmail.setText(email);
            etPhoneNumber.setText(phoneNumber);
            imgProfile.setImageResource(selectedAvatar);

        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_LONG).show();
            finish(); // Quay lại nếu không có người dùng
        }
    }

    private void showAvatarPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh đại diện");

        View view = getLayoutInflater().inflate(R.layout.dialog_avatar_picker, null);
        GridView gridView = view.findViewById(R.id.gvAvatars);
        AvatarAdapter adapter = new AvatarAdapter(this, avatars);
        gridView.setAdapter(adapter);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedAvatar = avatars[position];
            imgProfile.setImageResource(selectedAvatar);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveChanges() {
        SharedPreferences currentUserPrefs = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        String email = currentUserPrefs.getString("email", null);

        if (email != null) {
            SharedPreferences userAccountsPrefs = getSharedPreferences("USER_ACCOUNTS", MODE_PRIVATE);
            SharedPreferences.Editor editor = userAccountsPrefs.edit();

            String newFullName = etFullName.getText().toString();
            String newPhoneNumber = etPhoneNumber.getText().toString();

            editor.putString(email + "_fullName", newFullName);
            editor.putString(email + "_phoneNumber", newPhoneNumber);
            editor.putInt(email + "_avatar", selectedAvatar);
            editor.apply();

            Toast.makeText(this, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi: Không thể lưu thay đổi.", Toast.LENGTH_SHORT).show();
        }
    }
}
