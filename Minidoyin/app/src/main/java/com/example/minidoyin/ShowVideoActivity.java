package com.example.minidoyin;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

public class ShowVideoActivity extends AppCompatActivity {

    private String video_url;
    private Intent intent;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private SeekBar seekBar;
    private Runnable runnable;
    private Handler handler =new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                getProgess();
            }
        });
        intent = getIntent();
        video_url = intent.getStringExtra("video_url");
        mediaPlayer = new MediaPlayer();
        surfaceView =findViewById(R.id.surfaceview);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                else{
                    mediaPlayer.start();
                }
            }
        });

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new PlayerCallBack());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                getProgess();
            }
        });
        try {
            mediaPlayer.setDataSource(video_url);
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void getProgess(){
        runnable = new Runnable() {
            @Override
            public void run() {
                int progess = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(progess);
                handler.postDelayed(runnable,400);
            }
        };
        handler.post(runnable);
    }

    private class PlayerCallBack implements SurfaceHolder.Callback{
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mediaPlayer.setDisplay(holder);
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

}
