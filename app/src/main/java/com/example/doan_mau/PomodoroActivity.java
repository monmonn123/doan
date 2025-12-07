package com.example.doan_mau;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PomodoroActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = TimeUnit.MINUTES.toMillis(25);

    private TextView tvTimer;
    private Button btnStart, btnPause, btnStop;
    private ProgressBar progressBar;
    private CustomVideoView earthVideoView; // SỬA LỖI: Dùng CustomVideoView

    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        progressBar = findViewById(R.id.progressBar);
        earthVideoView = findViewById(R.id.earthVideoView); // SỬA LỖI: Ánh xạ đúng ID

        startEarthVideo();

        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnStop.setOnClickListener(v -> resetTimer());

        updateCountDownText();
    }

    private void startEarthVideo() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.earth);
        earthVideoView.setVideoURI(uri);
        earthVideoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0);
        });
        earthVideoView.start();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                updateButtons();
                // SỬA LỖI: Reset lại trạng thái của timer khi kết thúc
                timeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
                updateProgressBar();
            }
        }.start();

        timerRunning = true;
        updateButtons();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = START_TIME_IN_MILLIS;
        timerRunning = false;
        updateCountDownText();
        updateProgressBar();
        updateButtons();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    private void updateProgressBar() {
        progressBar.setProgress((int) (100 * timeLeftInMillis / START_TIME_IN_MILLIS));
    }

    private void updateButtons() {
        if (timerRunning) {
            btnStart.setVisibility(View.GONE);
            btnPause.setVisibility(View.VISIBLE);
        } else {
            btnStart.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // SỬA LỖI: Tự động tạm dừng timer nếu activity bị tạm dừng
        if (timerRunning) {
            pauseTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu lại video khi quay lại màn hình
        earthVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
