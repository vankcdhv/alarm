package com.example.alarmproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RunNotifyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");
        Intent notifyIntent = new Intent("android.intent.action.notifyAlarm");
        notifyIntent.putExtra("alarm", alarm);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notifyIntent);
    }
}