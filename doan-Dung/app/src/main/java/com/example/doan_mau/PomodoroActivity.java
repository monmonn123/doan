package com.example.doan_mau;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PomodoroActivity extends AppCompatActivity {

    private TextView tvTimer;
    private EditText edtMinutes;
    private Button btnStart, btnStop;
    private ProgressBar progressBar;

    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long mTimeLeftInMillis;
    private long mStartTimeInMillis; // Tổng thời gian (để tính %)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        // Ánh xạ View
        tvTimer = findViewById(R.id.tvTimer);
        edtMinutes = findViewById(R.id.edtMinutes);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        progressBar = findViewById(R.id.progressBar);

        // Nút Bắt đầu
        btnStart.setOnClickListener(v -> {
            String input = edtMinutes.getText().toString();
            if (TextUtils.isEmpty(input)) {
                Toast.makeText(this, "Nhập số phút đi bạn ơi!", Toast.LENGTH_SHORT).show();
                return;
            }

            long minutes = Long.parseLong(input);
            if (minutes == 0) {
                Toast.makeText(this, "Thời gian phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển phút sang mili giây
            mStartTimeInMillis = minutes * 60 * 1000;
            mTimeLeftInMillis = mStartTimeInMillis;

            startTimer();
        });

        // Nút Hủy
        btnStop.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        // Cài đặt thanh Progress Bar
        // Max là 10000 (để chạy mượt hơn)
        progressBar.setMax(10000);

        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                progressBar.setProgress(0);
                tvTimer.setText("00:00");
                updateButtons();
                edtMinutes.setEnabled(true);
                Toast.makeText(PomodoroActivity.this, "Hết giờ tập trung!", Toast.LENGTH_LONG).show();
            }
        }.start();

        timerRunning = true;
        updateButtons();
        edtMinutes.setEnabled(false); // Khóa ô nhập
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        mTimeLeftInMillis = 0;

        tvTimer.setText("00:00");
        progressBar.setProgress(10000); // Trả về đầy vòng tròn

        edtMinutes.setEnabled(true);
        updateButtons();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeFormatted);
    }

    private void updateProgressBar() {
        // Tính toán phần trăm còn lại để cập nhật vòng tròn
        // Công thức: (Thời gian còn / Tổng thời gian) * Max
        int progress = (int) ((mTimeLeftInMillis * 10000) / mStartTimeInMillis);
        progressBar.setProgress(progress);
    }

    private void updateButtons() {
        if (timerRunning) {
            btnStart.setVisibility(View.GONE); // Ẩn nút Bắt đầu
            btnStop.setVisibility(View.VISIBLE); // Hiện nút Hủy
            edtMinutes.setVisibility(View.INVISIBLE); // Ẩn ô nhập cho đỡ rối
        } else {
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
            edtMinutes.setVisibility(View.VISIBLE);
        }
    }
}