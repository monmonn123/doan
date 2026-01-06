package com.example.doan_mau;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan_mau.model.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class QAFragment extends Fragment {

    private RecyclerView rvQuestions;
    private QuestionAdapter adapter;
    private FloatingActionButton fabAddQuestion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa, container, false);

        rvQuestions = view.findViewById(R.id.rvQuestions);
        fabAddQuestion = view.findViewById(R.id.fabAddQuestion);

        setupRecyclerView();

        fabAddQuestion.setOnClickListener(v -> {
            // Mở màn hình đặt câu hỏi
            Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void setupRecyclerView() {
        // Phần này sẽ cập nhật để lấy từ API public posts sau
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("1", "Hệ thống", "Chào mừng bạn đến với mục Hỏi đáp!", 0, 0));
        
        adapter = new QuestionAdapter(questions);
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuestions.setAdapter(adapter);
    }
}