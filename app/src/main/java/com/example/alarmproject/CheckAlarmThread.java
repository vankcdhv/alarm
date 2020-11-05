package com.example.alarmproject;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CheckAlarmThread extends Thread {
    SimpleDateFormat dateFormat;
    DatabaseHandler databaseHandler;
    private Context context;
    private EvenListener evenListener;

    public CheckAlarmThread(Context context, EvenListener evenListener) {
        this.context = context;
        this.evenListener = evenListener;
    }

    public void run() {
        databaseHandler = new DatabaseHandler(context, null, null, 1);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        while (true) {
            Date now = new Date();
            if (now.getSeconds() == 0) {
                evenListener.showToast();
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
                        evenListener.notifyAlarm(alarm);
                    }
                }
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public interface EvenListener {
        public void notifyAlarm(Alarm alarm);

        public void showToast();
    }

}
