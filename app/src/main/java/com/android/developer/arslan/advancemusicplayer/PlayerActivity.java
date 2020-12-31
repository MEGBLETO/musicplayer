package com.android.developer.arslan.advancemusicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.quicksettings.Tile;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.jar.Manifest;

import Model.Song;
import Request.SoundcloudApiReq;

public class PlayerActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Boolean isAccelerometerSensorAvailable , itIsNotTheSameValue = false;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private float shakeThresold =5f;
    private Vibrator vibrator;
    private static  final String TAG = "APP";

    static MediaPlayer mp;//assigning memory loc once or else multiple songs will play at once
    int position;
    SeekBar sb;
    ArrayList<File> mySongs;
    Thread updateSeekBar;
    Button pause,next,previous;
    TextView songNameText;
    TextView currentTime;
    TextView endTime;

    String sname;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_ui);

        songNameText = (TextView) findViewById(R.id.txtSongLabel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Now Playing");

        pause = (Button)findViewById(R.id.pause);

        previous = (Button)findViewById(R.id.previous);
        next = (Button)findViewById(R.id.next);

        sb=(SeekBar)findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTime);
        endTime = findViewById(R.id.endTime);


        updateSeekBar=new Thread(){
            @Override
            public void run(){
                int totalDuration = mp.getDuration();
               /* //set the max durationfor the currently playing song
                  int totTime = mp.getDuration();
                endTime.setText(totTime);
*/
                int currentPosition = 0;
                while(currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition=mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }
                    catch (InterruptedException e){

                        }
                }
            }
        };




        if(mp != null){
            mp.stop();
            mp.release();
        }
        Intent i = getIntent();
        Bundle b = i.getExtras();


        mySongs = (ArrayList) b.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();

        String SongName = i.getStringExtra("songname");
        songNameText.setText(SongName);
        songNameText.setSelected(true);

        position = b.getInt("pos",0);
        Uri u = Uri.parse(mySongs.get(position).toString());

        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekBar.start();
        sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        sb.setOnSeekBarChangeListener(new
                                              SeekBar.OnSeekBarChangeListener() {
                                                  @Override
                                                  public void onProgressChanged(SeekBar seekBar, int i,
                                                                                boolean b) {
                                                  }
                                                  @Override
                                                  public void onStartTrackingTouch(SeekBar seekBar) {
                                                  }
                                                  @Override
                                                  public void onStopTrackingTouch(SeekBar seekBar) {
                                                      mp.seekTo(seekBar.getProgress());

                                                  }
                                              });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setMax(mp.getDuration());
                if(mp.isPlaying()){
                    pause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    mp.pause();

                }
                else {
                    pause.setBackgroundResource(R.drawable.pause);
                    mp.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();
                position=((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get( position).toString());
               // songNameText.setText(getSongName);
                mp = MediaPlayer.create(getApplicationContext(),u);

                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);

                try{
                    mp.start();
                }catch(Exception e){}

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songNameText.setText(getSongName);
                mp.stop();
                mp.release();

                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);
                mp.start();
            }
        });

         vibrator = (Vibrator) getSystemService((Context.VIBRATOR_SERVICE));
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        }
           else{
              // System.out.println('censor not available');
            isAccelerometerSensorAvailable= false;

        }

    }

     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
           onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentX= sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if(itIsNotTheSameValue){
           xDifference = Math.abs(lastX -currentX);
           yDifference = Math.abs(lastY-currentY);
           zDifference = Math.abs(lastZ-currentZ);

           if((xDifference > shakeThresold && yDifference > shakeThresold) || (xDifference > shakeThresold && zDifference >shakeThresold) || (yDifference >shakeThresold && zDifference > shakeThresold)){
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                   mp.stop();
                   mp.release();
                   position=((position+1)%mySongs.size());
                   Uri u = Uri.parse(mySongs.get( position).toString());
                   // songNameText.setText(getSongName);
                   mp = MediaPlayer.create(getApplicationContext(),u);

                   sname = mySongs.get(position).getName().toString();
                   songNameText.setText(sname);

                   try{
                       mp.start();
                   }catch(Exception e){}

               }
               else {
                   vibrator.vibrate(500);

               }
           }
        }

        lastX= currentX;
        lastY = currentY;
        lastZ= currentZ;
        itIsNotTheSameValue =true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isAccelerometerSensorAvailable){
            sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isAccelerometerSensorAvailable){
            sensorManager.unregisterListener((this));
        }
    }

    public void getSongList(){
        RequestQueue queue = volleySingleton.getInstance(this).getmRequestQueue();

        SoundcloudApiReq request = new SoundcloudApiReq(queue);

        request.getSongList(new SoundcloudApiReq.DeezerInterface() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                Log.d(TAG, "onSuccess: " + songs);            }

            @Override
            public void onError(String message) {

                Toast.makeText(PlayerActivity.this, message,Toast.LENGTH_SHORT).show();

            }
        });
    }

/*

    public String createTimerLabel(int duration){
        String timerLabel= "";
        int min = duration / 1000 /60;
        int sec = duration /1000 % 60;

        timerLabel += min + ":";

        if(sec < 10) timerLabel += "0";
        timerLabel += sec;

        return timerLabel;
    }*/
}