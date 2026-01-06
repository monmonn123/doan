package com.example.doan_mau;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicPlayerActivity extends AppCompatActivity {
    // Đổi sang RecyclerView để dùng giao diện xịn
    RecyclerView rvMusic;
    Button btnStop;
    MediaPlayer mediaPlayer;

    String[] songNames = {"Chill Lofi", "Lofi Ambient Study", "Lofi Background", "Lofi Chill", "Rain Sound"};
    int[] songFiles = {R.raw.chill_lofi, R.raw.lofi_ambient_study, R.raw.lofi_background_music, R.raw.lofi_chill, R.raw.rain_sound};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Fix lỗi: Tìm đúng ID rvMusic trong file XML mới
        rvMusic = findViewById(R.id.rvMusic);
        btnStop = findViewById(R.id.btnStopMusic);

        // Thiết lập hiển thị danh sách dạng thẻ cuộn
        rvMusic.setLayoutManager(new LinearLayoutManager(this));

        MusicAdapter adapter = new MusicAdapter(songNames, position -> {
            stopMusic();
            mediaPlayer = MediaPlayer.create(MusicPlayerActivity.this, songFiles[position]);
            mediaPlayer.start();
            Toast.makeText(this, "Đang phát: " + songNames[position], Toast.LENGTH_SHORT).show();
        });

        rvMusic.setAdapter(adapter);

        btnStop.setOnClickListener(v -> {
            stopMusic();
            Toast.makeText(this, "Đã dừng nhạc", Toast.LENGTH_SHORT).show();
        });
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }
}