package com.example.alarmproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service {
    private DatabaseHandler databaseHandler;
    private SimpleDateFormat dateFormat;
    private Timer timer;
    private TimerTask timerTask;

    public AlarmService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(2, notification);
    }


    private boolean equal(Date date1, Date date2) {
        if (date1.getDate() == date2.getDate() && date1.getHours() == date2.getHours() && date1.getMinutes() == date2.getMinutes())
            return true;
        return false;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                databaseHandler = new DatabaseHandler(AlarmService.this, null, null, 1);
                dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date now = new Date();
                if (now.getSeconds() == 0) {
                    Log.d("threadLog", new Date().toString());
                    List<Alarm> alarmList = databaseHandler.getListAlarm();
                    alarmList.sort(new Comparator<Alarm>() {
                        @Override
                        public int compare(Alarm o1, Alarm o2) {
                            Date alarmDate_o1 = new Date();
                            Date alarmDate_o2 = new Date();
                            try {
                                int hour_o1 = Integer.parseInt(o1.getTime().split(":")[0].trim());
                                int minute_o1 = Integer.parseInt(o1.getTime().split(":")[1].trim());

                                alarmDate_o1 = dateFormat.parse(o1.getDate());
                                alarmDate_o1.setHours(hour_o1);
                                alarmDate_o1.setMinutes(minute_o1);

                                int hour_o2 = Integer.parseInt(o2.getTime().split(":")[0].trim());
                                int minute_o2 = Integer.parseInt(o2.getTime().split(":")[1].trim());

                                alarmDate_o2 = dateFormat.parse(o2.getDate());
                                alarmDate_o2.setHours(hour_o2);
                                alarmDate_o2.setMinutes(minute_o2);


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return alarmDate_o1.compareTo(alarmDate_o2);
                        }
                    });
                    for (int i = 0; i < alarmList.size(); i++) {
                        Alarm alarm = alarmList.get(i);
                        if (alarm.isActive()) {
                            Date alarmDate = null;
                            try {
                                int hour = Integer.parseInt(alarm.getTime().split(":")[0].trim());
                                int minute = Integer.parseInt(alarm.getTime().split(":")[1].trim());
                                alarmDate = dateFormat.parse(alarm.getDate());
                                alarmDate.setHours(hour);
                                alarmDate.setMinutes(minute);
                                Date currentTime = new Date();
                                if (equal(alarmDate, currentTime)) {
                                    Log.d("threadLog", "Alarm: " + alarmDate.toString() + " - " + new Date().toString());
                                    Intent notifyIntent = new Intent("android.intent.action.notifyAlarm");
                                    notifyIntent.putExtra("alarm", alarm);
                                    notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    AlarmService.this.startActivity(notifyIntent);

//                                    Intent broadcastIntent = new Intent();
//                                    broadcastIntent.putExtra("alarm", alarm);
//                                    broadcastIntent.setAction("runNotify");
//                                    broadcastIntent.setClass(AlarmService.this, RunNotifyReceiver.class);
//                                    sendBroadcast(broadcastIntent);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        };
        timer.schedule(timerTask, 0, 1000);

    }

    private void createNotify(Alarm alarm) {
        Intent notifyIntent = new Intent("android.intent.action.notify");
        notifyIntent.putExtra("alarm", alarm);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(AlarmService.this, 0, notifyIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AlarmService.this, Const.CHANEL_ID)
                .setSmallIcon(R.drawable.ic_action_access_alarms)
                .setContentTitle("Báo Thức")
                .setContentText(alarm.getTitle() + "")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Uri alarmSound = Uri.parse(alarm.getRingtone());
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        if (alarm.isVibrate()) {
            builder.setVibrate(pattern);
        }

        Notification notifyAlarm = builder.build();
        notifyAlarm.sound = alarmSound;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AlarmService.this);
        notificationManager.notify(10001, notifyAlarm);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.d("threadLog", "Destroyed");
        stoptimertask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, MyBroadcastReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Const.CHANEL_NAME;
            String description = Const.CHANEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Const.CHANEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
