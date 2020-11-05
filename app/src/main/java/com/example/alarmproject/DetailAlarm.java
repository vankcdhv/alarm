package com.example.alarmproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailAlarm extends AppCompatActivity {
    Calendar calendar = Calendar.getInstance();
    private TimePicker timePicker;
    private TextView textViewRingtone, textViewRepeat;
    private EditText editTextTitle;
    private Switch switchVibrate;


    private String mode;
    private Alarm alarm;

    private Integer alarmHour;
    private Integer alarmMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_alarm);

        editTextTitle = (EditText) findViewById(R.id.editTextTextPersonName);
        textViewRepeat = findViewById(R.id.textViewRepeat);
        showDate();
        textViewRingtone = findViewById(R.id.textViewRingtone);
        switchVibrate = (Switch) findViewById(R.id.switchVibrate);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                alarmHour = hourOfDay;
                alarmMinute = minute;
            }
        });
        init();

    }

    private void init() {
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        if (mode.equals(Const.MODE_ADD)) {
            alarm = new Alarm();
        } else {
            alarm = (Alarm) intent.getSerializableExtra("alarm");
            timePicker.setHour(Integer.parseInt(alarm.getTime().split(":")[0].trim()));
            timePicker.setMinute(Integer.parseInt(alarm.getTime().split(":")[1].trim()));
            editTextTitle.setText(alarm.getTitle());
            textViewRepeat.setText(alarm.getDate());
            Uri uri = Uri.parse(alarm.getRingtone());
            textViewRingtone.setText(RingtoneManager.getRingtone(this, uri).getTitle(this));
            switchVibrate.setChecked(alarm.isVibrate());

        }
    }

    public void cancelOnClick(View view) {
        onBackPressed();
    }

    public void saveOnClick(View view) {

        try {

            DatabaseHandler handler = new DatabaseHandler(this, null, null, 1);

            String title = editTextTitle.getText().toString();
            String time = (timePicker.getHour() + ":" + timePicker.getMinute()).trim();
            String dateString = textViewRepeat.getText().toString();
            String ringtone = uri.toString();
            boolean vibrate = switchVibrate.isChecked();
            alarm = new Alarm(alarm.getId(), title, time, dateString, ringtone, vibrate, true);

            if (mode.equals(Const.MODE_ADD)) {
                Long result = handler.addAlarm(alarm);
                if (result == -1) {
                    Toast.makeText(this, "ADD FAIL", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "ADD SUCCESS", Toast.LENGTH_SHORT).show();
            } else {
                int result = handler.updateAlarm(alarm);
                if (result == -1) {
                    Toast.makeText(this, "UPDATE FAIL", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "UPDATE SUCCESS", Toast.LENGTH_SHORT).show();
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("alarm", alarm);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ringtoneOnClick(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, Const.REQUEST_CODE_RINGTONE);
    }

    public Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == Const.REQUEST_CODE_RINGTONE) {
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                textViewRingtone.setText(RingtoneManager.getRingtone(this, uri).getTitle(this));
            } else {
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                textViewRingtone.setText("");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showDate();
        }
    };

    public void calendarOnClick(View view) {
        new DatePickerDialog(DetailAlarm.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        textViewRepeat.setText(format.format(calendar.getTime()));
    }

}