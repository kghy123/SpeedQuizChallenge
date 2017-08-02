package com.kghy1234gmail.speedquizchallenge;

import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.VideoView;

public class RecordPreviewActivity extends AppCompatActivity {

    VideoView videoView;

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_record_preview);

        videoView = (VideoView)findViewById(R.id.record_preview);

        videoView.setMediaController(new android.widget.MediaController(this));

        path = getIntent().getStringExtra("path");

        Log.e("path", path);

        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();


            }
        });

    }
}
