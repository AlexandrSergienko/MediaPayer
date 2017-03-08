package com.vennich.mediapayer; //FIXME missed character

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView tvForTry;
    private MyService myService;
    private boolean isConnected;
    private Handler handler = new Handler();

    private TextView tvCurrentTime;
    private TextView tvAllTime;
    private SeekBar seekBar;
    private ImageView imageView2;

//FIXME change name of the function. It's not clear what this function actually does
    private String formatTimeFormat(int duration /*it's better to change this parameter to long and change the name of parameter to clarified TimeUnit at seconds or milliseconds*/){
        StringBuilder sb = new StringBuilder();
        int seconds = duration / 1000 /*what are the magic numbers? You should use constants for all magic numbers and it'd be better to use JODA library to operate with dates */;
        if(seconds / 60 > 9){
            sb.append(seconds / 60).append(":");
        }else if(seconds / 60 > 0){
            sb.append("0").append(seconds / 60).append(":");
        }else {
            sb.append("00:");
        }
        if(seconds % 60 > 9){
            sb.append(seconds % 60);
        }else if(seconds % 60 > 0){
            sb.append("0").append(seconds % 60);
        }else {
            sb.append("00");
        }
        return sb.toString();
    }
    private void updateSeekBar(){
        int mSecond = myService.getCurrentPosition();
        seekBar.setProgress(mSecond);
        tvCurrentTime.setText(formatTimeFormat(mSecond));
    }
    private void initialize(){
        tvCurrentTime = (TextView)findViewById(R.id.tvCurrentTime);
        tvAllTime = (TextView)findViewById(R.id.tvAllTime);
        seekBar = (SeekBar)findViewById(R.id.sbProgress);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar sb = (SeekBar)v;
                int millisecond = sb.getProgress();
                myService.seekTo(millisecond);
                seekBar.setProgress(millisecond);
                return false;
            }
        });
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        imageView2.setImageResource(R.drawable.cover);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }


    @Override
    protected void onStart() {
        super.onStart();

        //connect to MyService
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //disconnection from MyService
        if (isConnected) { 
            //FIXME you should unbind the service at the same life cycle method
            unbindService(serviceConnection);
            isConnected = false;
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        //when connected
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = ((MyService.LocalBinder) service).getService();
            isConnected = true;

            int duration = myService.getDuration();
            seekBar.setMax(duration);
            tvAllTime.setText(formatTimeFormat(duration));
            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateSeekBar();
                    handler.postDelayed(this, 1000);
                }
            });
        }


        //when disconnected
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
        }
    };

    public void stop_click(View view) { //FIXME method names with Java Code Convention
        myService.changeAudioState(2);
    }
    public void play_click(View view) {
        myService.changeAudioState(0);
    }
    public void pause_click(View view) {
        myService.changeAudioState(1);
    }
}
