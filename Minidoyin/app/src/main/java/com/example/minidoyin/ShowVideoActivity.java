package com.example.minidoyin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
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

    private int video_width;
    private int video_height;
    private int surfaceWidth;
    private int surfaceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

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

        //进度条设置
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

    private void openvideo(){
        intent = getIntent();
        video_url = intent.getStringExtra("video_url");

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

    private class PlayerCallBack implements SurfaceHolder.Callback{
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mediaPlayer.setDisplay(holder);
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                surfaceWidth=surfaceView.getWidth();
                surfaceHeight=surfaceView.getHeight();
            }else {
                surfaceWidth=surfaceView.getHeight();
                surfaceHeight=surfaceView.getWidth();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    changesize();
                }
            });
            openvideo();
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    private void changesize(){
        video_height = mediaPlayer.getVideoHeight();
        video_width = mediaPlayer.getVideoWidth();
        float max;
        if(getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            //竖屏
            max = Math.max((float) video_width / (float) surfaceWidth,(float) video_height / (float) surfaceHeight);
        }else{
            //横屏
            max = Math.max(((float) video_width/(float) surfaceHeight),(float) video_height/(float) surfaceWidth);
        }
        video_width = (int) Math.ceil((float) video_width / max);
        video_height = (int) Math.ceil((float) video_height / max);
        Log.i("1", "changesize: "+video_width+"   "+video_height);
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(video_width,video_height));

    }



}
