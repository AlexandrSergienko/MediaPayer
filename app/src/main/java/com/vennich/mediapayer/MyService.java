package com.vennich.mediapayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();

    private void initialize(){
        mediaPlayer = MediaPlayer.create(this, R.raw.counting_blue_cars);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }


    public void changeAudioState(int state){
        switch (state){
            case 0:
                mediaPlayer.start();
                break;
            case 1:
                mediaPlayer.pause();
                break;
            case 2:
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                break;
            default:
                Log.i("changeAudioState", "illegal state: " + state);
        }
    }
    public void seekTo(int millisecond){
        mediaPlayer.seekTo(millisecond);
    }
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }


    public class LocalBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }
}
