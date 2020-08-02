package com.example.roboticfanvoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView animationView;
    boolean fanRotating = true;
    boolean isRecording = false;
    AudioManager am = null;
    AudioRecord record = null;
    AudioTrack track = null;
    PlaybackParams pbp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbp = new PlaybackParams();
        pbp.setPitch(2f);
        pbp.setSpeed(1f);
        animationView = findViewById(R.id.animationView);
       // animationView.cancelAnimation();
        checkPermisIsion();
     //   initRecordAndTrack();
        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(fanRotating){
                  stopRecordAndPlay();
                  fanRotating  = false;
                  animationView.cancelAnimation();

              }
              else{
                  startRecordAndPlay();
                  fanRotating  = true;
                  animationView.playAnimation();

              }
                }
        });

        setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);



        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);

        (new Thread()
        {
            @Override
            public void run()
            {
                recordAndPlay();
            }
        }).start();



    }

    private void checkPermisIsion() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1234);
        }
    }

    private void initRecordAndTrack()
    {
        int min = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                min);

        int maxJitter = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, maxJitter,
                AudioTrack.MODE_STREAM);
        track.setPlaybackParams(pbp);
    }

    private void recordAndPlay()
    {
        short[] lin = new short[1024];
        int num = 0;
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        while (true)
        {
            if (isRecording)
            {
                num = record.read(lin, 0, 1024);
                track.write(lin, 0, num);
            }
        }
    }

    private void startRecordAndPlay()
    {
        record.startRecording();
        track.play();
        isRecording = true;
    }

    private void stopRecordAndPlay()
    {
        record.stop();
        track.pause();
        isRecording = false;
    }
}