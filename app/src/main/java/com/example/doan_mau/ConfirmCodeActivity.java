package com.example.doan_mau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmCodeActivity extends AppCompatActivity {

    private EditText edtConfirmationCode;
    private Button btnConfirm;
    private TextView tvInstruction, tvResendCode;

    private final String MOCK_CODE = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);

        edtConfirmationCode = findViewById(R.id.edtConfirmationCode);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvResendCode = findViewById(R.id.tvResendCode);

        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        String userPassword = getIntent().getStringExtra("USER_PASSWORD");
        String userFullName = getIntent().getStringExtra("USER_FULL_NAME");
        String userStudentId = getIntent().getStringExtra("USER_STUDENT_ID");

        if (userEmail != null) {
            tvInstruction.setText("Một mã xác thực đã được gửi đến email " + userEmail + ". Vui lòng nhập mã vào ô bên dưới.");
        }

        btnConfirm.setOnClickListener(v -> {
            String enteredCode = edtConfirmationCode.getText().toString();
            if (enteredCode.equals(MOCK_CODE)) {

                SharedPreferences prefs = getSharedPreferences("USER_ACCOUNTS", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                
                // Dùng email làm "key" chính để lưu các thông tin khác
                editor.putString(userEmail + "_password", userPassword);
                editor.putString(userEmail + "_fullName", userFullName);
                editor.putString(userEmail + "_studentId", userStudentId);
                editor.apply();

                Toast.makeText(this, "Xác thực thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ConfirmCodeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Mã xác thực không đúng!", Toast.LENGTH_SHORT).show();
            }
        });

        tvResendCode.setOnClickListener(v -> {
            Toast.makeText(this, "Đã gửi lại mã xác thực.", Toast.LENGTH_SHORT).show();
        });
    }
}
