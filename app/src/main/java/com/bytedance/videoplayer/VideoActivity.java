package com.bytedance.videoplayer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Date;
import java.util.Timer;

public class VideoActivity extends AppCompatActivity {
    VideoView videoView;
    LinearLayout LL;
    ImageButton IB;
    SeekBar SB;
    TextView tvNow;
    TextView tvLength;
    static int position = 0;
    static long ti = 0;
    Handler handler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        videoView = findViewById(R.id.videoView);
        LL = findViewById(R.id.LL);
        IB = findViewById(R.id.IB);
        SB = findViewById(R.id.seekBar);
        tvNow = findViewById(R.id.textViewNow);
        tvLength = findViewById(R.id.textViewLength);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView != null) {
                    //handler.removeCallbacks(r);
                    LL.setVisibility(View.VISIBLE);
                    IB.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LL.setVisibility(View.INVISIBLE);
                            IB.setVisibility(View.INVISIBLE);
                        }
                    }, 2000);
                    return true;
                }
                return false;
            }
        });

        IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    IB.setImageResource(R.drawable.play);
                } else {
                    IB.setImageResource(R.drawable.stop);
                    videoView.start();
                }
            }
        });

        SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(videoView.getDuration() * progress / 100);
                    if (!videoView.isPlaying())
                        videoView.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Intent intent = getIntent();
        videoView.setVideoURI(intent.getData());
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                tvLength.setText(getTimeFormat(videoView.getDuration()));
            }
        });
        if (System.currentTimeMillis() - ti < 1000)
            videoView.seekTo(position);
        videoView.start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView != null && videoView.isPlaying()) {
                    SB.setProgress(videoView.getCurrentPosition() * 100 / videoView.getDuration());
                    position = videoView.getCurrentPosition();
                    ti = System.currentTimeMillis();
                    tvNow.setText(getTimeFormat(videoView.getCurrentPosition()));
                }
                handler.postDelayed(this, 500);
            }
        }, 100);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LL.setVisibility(View.INVISIBLE);
                IB.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }

    public static String getTimeFormat(long msec) {
        long sec = msec / 1000;
        long min = sec / 60;
        long hour = min / 60;
        sec %= 60;
        min %= 60;
        String res = "";
        if (hour > 0)
            res += hour + ":";
        if (min > 0 || hour > 0)
            res += min + ":";
        res += sec;
        return res;
    }


}

