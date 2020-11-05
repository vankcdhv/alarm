package com.example.alarmproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private final int NOTIFICATION_ID = 100;
    private final String NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        Intent data = new Intent(context, NotifyActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, data, 0);


//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
//                        .setContentTitle("Ring ring")
//                        .setContentText("Day de")
//                        .setColor(Color.rgb(0, 0, 255))
//                        .setSmallIcon(android.R.drawable.ic_dialog_info)
//                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent)
//                        .setPriority(NotificationCompat.PRIORITY_MAX);
//
        Uri uri = (Uri) intent.getParcelableExtra("uri");
//
//        builder.setSound(uri);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(100001, builder.build());


//        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, "Alarm ring ring!!!", NotificationManager.IMPORTANCE_HIGH);
//        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        manager.createNotificationChannel(channel);
//        manager.notify(NOTIFICATION_ID, builder.build());
//
        Ringtone r = RingtoneManager.getRingtone(context, uri);
        r.play();
        r.setLooping(true);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Const.CHANEL_NAME;
            String description = Const.CHANEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Const.CHANEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
