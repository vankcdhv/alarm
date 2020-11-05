package com.example.alarmproject;

import java.io.Serializable;

public class Alarm implements Serializable {
    private String id;
    private String title;
    private String time;
    private String date;
    private String ringtone;
    private String uri;
    private boolean vibrate;
    private boolean active;

    public Alarm() {
    }

    public Alarm(String title, String time, String date, String ringtone, boolean vibrate, boolean active) {
        this.title = title;
        this.time = time;
        this.date = date;
        this.ringtone = ringtone;
        this.vibrate = vibrate;
        this.active = active;
    }

    public Alarm(String id, String title, String time, String date, String ringtone, boolean vibrate, boolean active) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.date = date;
        this.ringtone = ringtone;
        this.vibrate = vibrate;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
