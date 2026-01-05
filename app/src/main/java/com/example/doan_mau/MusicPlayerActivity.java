package com.example.doan_mau;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MusicPlayerActivity extends AppCompatActivity {
    private ListView listViewMusic;
    private TextView tvCurrentSong;
    private Button btnStopMusic;
    private static MediaPlayer mediaPlayer; // Static để nhạc chạy ngầm mượt mà

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player); // Hết đỏ sau khi sửa XML

        listViewMusic = findViewById(R.id.listViewMusic);
        tvCurrentSong = findViewById(R.id.tvCurrentSong);
        btnStopMusic = findViewById(R.id.btnStopMusic);

        String[] songNames = {"Playlist Nhạc Chill 2 Giờ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songNames);
        listViewMusic.setAdapter(adapter);

        listViewMusic.setOnItemClickListener((parent, view, position, id) -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                // MediaPlayer.create nên chạy trong try-catch để tránh treo App
                mediaPlayer = MediaPlayer.create(this, R.raw.nhacthugian);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                    tvCurrentSong.setText("Đang phát: " + songNames[0]);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi phát nhạc!", Toast.LENGTH_SHORT).show();
            }
        });

        btnStopMusic.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                tvCurrentSong.setText("Đã dừng");
            }
        });
    }
}