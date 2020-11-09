package com.example.alarmproject;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NotifyActivity extends AppCompatActivity {

    private Button btnStop;
    private Ringtone r;
    private boolean isFinish;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        init();
    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    private void init() {
        isFinish = false;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinish) {
                    Toast.makeText(NotifyActivity.this, "Có cố gắng, nhưng không tắt được đâu", Toast.LENGTH_SHORT).show();
                } else{
                    onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        btnStop = findViewById(R.id.btnStopAlarm);

        Intent intent = getIntent();

        Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");

        Uri uri = Uri.parse(alarm.getRingtone());
        r = RingtoneManager.getRingtone(this, uri);
        r.setLooping(true);
        r.play();
    }


    public void onClickBtnStopAlarm(View v) {
        r.stop();
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        isFinish = true;
        finish();
    }
}