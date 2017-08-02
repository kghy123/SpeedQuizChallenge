package com.kghy1234gmail.speedquizchallenge;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class IngameActivity extends AppCompatActivity implements SensorEventListener {

    String key;

    Sensor sensor;
    SensorManager sensorManager;

    boolean isPaused = false;


    RecordView recordView;

    ArrayList<String> contents = new ArrayList<>();
    int cnt = 0;
    ArrayList<Integer> corrects = new ArrayList<>();

    GameThread gameThread;

    ImageView img;
    TextView timerTv;
    TextView contentTv;

    boolean isNext = false;

    SoundPool pool;
    int[] sounds = new int[4];
    float soundVol = 0.07f;

    public void soundLoad(){
        pool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        sounds[0] = pool.load(this, R.raw.next, 1);
        sounds[1] = pool.load(this, R.raw.correct, 1);
        sounds[2] = pool.load(this, R.raw.wrong, 1);
        sounds[3] = pool.load(this, R.raw.end, 1);
    }

    AudioManager audioManager;
    Vibrator vibrator;
    int vibratorTime = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ingame);

        soundLoad();

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);


        recordView = (RecordView)findViewById(R.id.ingame_recordview);
        timerTv = (TextView)findViewById(R.id.ingame_tv_timer);
        contentTv = (TextView)findViewById(R.id.ingame_tv_content);

        img = (ImageView)findViewById(R.id.img);

        Glide.with(this).load(R.drawable.whiteboard).into(img);
        img.setAlpha(0.9f);

        key = getIntent().getStringExtra("key");

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        readJson();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(IngameActivity.this, sensor, SensorManager.SENSOR_DELAY_UI);
        if(gameThread!=null)gameThread.unpause();


}

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);

        super.onPause();
        gameThread.isRun= true;
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(gameThread.isRun){
            File file = new File(recordView.path);
            file.delete();
            Log.d("FILE", "의도치않게 종료 파일 삭제");
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);

        //의도치 않게 종료되었을 시에 파일 삭제
        if(gameThread.isRun){
            File file = new File(recordView.path);
            file.delete();
            Log.d("FILE", "의도치않게 종료 파일 삭제");
            gameThread.isRun = false;
            recordView.stopRec();
        }


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if(Math.atan2(x, z) * 180/Math.PI> 80 && Math.atan2(x, z) * 180/Math.PI < 100) {

            if(gameThread == null){
                gameThread = new GameThread();

                gameThread.start();
            }
//            Log.e("in", "in");
            if(!isNext){
                isNext = true;
                if(cnt == contents.size()) cnt--;

                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) pool.play(sounds[0], 0.2f, 0.2f, 3, 0, 1);
                else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) vibrator.vibrate(vibratorTime);
                contentTv.setText(contents.get(cnt));
            }

        }

        if(Math.atan2(x, z) * 180/Math.PI > 150){

//            Log.d("correct", "correct");
            if(isNext){
                isNext = false;

                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) pool.play(sounds[1], soundVol, soundVol, 3, 0 , 1);
                else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) vibrator.vibrate(vibratorTime);

                contentTv.setText("CORRECT");
                if(corrects.size() < contents.size()) corrects.add(1);
                cnt++;
            }

        }else if(Math.atan2(x, z) * 180/Math.PI < 30){

//            Log.d("wrong", "wrong");
            if(isNext){
                isNext = false;

                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) pool.play(sounds[2], soundVol, soundVol, 3, 0, 1);
                else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) vibrator.vibrate(vibratorTime);

                contentTv.setText("WRONG");
                if(corrects.size() < contents.size()) corrects.add(0);
                cnt++;
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void readJson(){
        try {
            FileInputStream fis = openFileInput("themes.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            StringBuffer buffer = new StringBuffer();
            String line = br.readLine();
            while(line != null){
                buffer.append(line);
                line = br.readLine();
            }


            JSONObject obj = new JSONObject(buffer.toString());
            obj = obj.getJSONObject(key);
            JSONArray array = obj.getJSONArray("contents");

            for(int i=0; i<array.length(); i++){
                contents.add(array.getString(i));
            }
        } catch (Exception e) {
            Log.e("error", "error");
        }

        shuffle();
    }

    public void shuffle(){

        Random rnd = new Random();
        for(int i=0; i<contents.size(); i++){

            String tmp = contents.get(i);
            int tmpInt = rnd.nextInt(contents.size());
            contents.set(i, contents.get(tmpInt));
            contents.set(tmpInt, tmp);

        }
    }

    class GameThread extends Thread{

        int s = 5;
        int ns = 0;

        boolean isRun = true;

        @Override
        public void run() {

            //타이머 시작!!
            isRun = true;

            while(recordView.isRecording) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerTv.setText(String.format("%02d:%02d", s, ns));
                    }
                });
                if (ns == 0) {
                    ns = 99;
                    s--;
                }
                ns--;

                if (s == 0 && ns == 0) {
                    if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) pool.play(sounds[3], soundVol, soundVol, 6, 0, 1);
                    else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) vibrator.vibrate(2000);

                    recordView.stopRec();

                    Intent intent = new Intent(IngameActivity.this, GameEndActivity.class);
                    intent.putStringArrayListExtra("contents", contents);
                    intent.putExtra("path", recordView.path);
                    intent.putIntegerArrayListExtra("corrects", corrects);
                    intent.putExtra("key", key);

                    IngameActivity.this.startActivity(intent);

                    isRun = false;

                    finish();
                }

                try {sleep(10);} catch (InterruptedException e) {}

                synchronized (this){
                    if(isPaused){
                        try {wait();} catch (InterruptedException e) {}
                    }
                }
            }


        }

        public void pause(){
            isPaused = true;
        }

        public void unpause(){
            isPaused = false;

            synchronized (this){
                this.notify();
            }
        }
    }


}
