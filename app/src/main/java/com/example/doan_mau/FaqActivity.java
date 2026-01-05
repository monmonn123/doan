package com.example.doan_mau;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    private RecyclerView rvQuestions;
    private QuestionAdapter adapter;
    private List<Question> questionList;
    private FloatingActionButton fabAddQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        rvQuestions = findViewById(R.id.rvQuestions);
        fabAddQuestion = findViewById(R.id.fabAddQuestion);

        questionList = new ArrayList<>();
        // Mock data để test giao diện trước khi kết nối API
        questionList.add(new Question("Nguyễn Văn A", "Làm sao để đăng ký học phần bổ sung ạ?", 12, 1, 5));
        questionList.add(new Question("Trần Thị B", "Cho em hỏi lịch nghỉ Tết chính thức của trường mình với ạ.", 45, 0, 10));
        questionList.add(new Question("Lê Văn C", "App MyUTE bị lỗi không đăng nhập được trên Android 14?", 3, 15, 2));

        adapter = new QuestionAdapter(questionList);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(adapter);

        fabAddQuestion.setOnClickListener(v -> showAddQuestionDialog());
    }

    private void showAddQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_question, null);
        builder.setView(dialogView);

        EditText etQuestionContent = dialogView.findViewById(R.id.etQuestionContent);

        builder.setPositiveButton("Đăng", (dialog, which) -> {
            String content = etQuestionContent.getText().toString().trim();
            if (!content.isEmpty()) {
                // Thêm vào danh sách tạm thời (Sau này sẽ gọi API POST)
                questionList.add(0, new Question("Tôi (Sinh viên)", content, 0, 0, 0));
                adapter.notifyItemInserted(0);
                rvQuestions.scrollToPosition(0);
                Toast.makeText(this, "Đã đăng câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
