package com.example.alarmproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AlarmService extends Service {
    private DatabaseHandler databaseHandler;
    private SimpleDateFormat dateFormat;

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean equal(Date date1, Date date2) {
        if (date1.getDate() == date2.getDate() && date1.getHours() == date2.getHours() && date1.getMinutes() == date2.getMinutes())
            return true;
        return false;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        new CheckAlarmThread(this, new CheckAlarmThread.EvenListener() {
            @Override
            public void notifyAlarm(Alarm alarm) {
                Date alarmDate = null;
                try {
                    int hour = Integer.parseInt(alarm.getTime().split(":")[0].trim());
                    int minute = Integer.parseInt(alarm.getTime().split(":")[1].trim());
                    alarmDate = dateFormat.parse(alarm.getDate());
                    alarmDate.setHours(hour);
                    alarmDate.setMinutes(minute);
                    Date currentTime = new Date();
                    Log.d("threadLog", "Alarm: " + alarmDate.toString() + " - " + new Date().toString());
                    if (equal(alarmDate, currentTime)) {
                        Intent notifyIntent = new Intent(AlarmService.this, NotifyActivity.class);
                        notifyIntent.putExtra("alarm", alarm);
                        startActivity(notifyIntent);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void showToast() {
                Log.d("threadLog", new Date().toString());

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Const.CHANEL_NAME;
            String description = Const.CHANEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Const.CHANEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
