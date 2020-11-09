package com.example.alarmproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ArrayList<Alarm> list;
    AlertDialog.Builder builder;

    private Intent mServiceIntent;
    private AlarmService alarmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.floatingActionButton);
        builder = new AlertDialog.Builder(this);

        final DatabaseHandler handler = new DatabaseHandler(this, null, null, 1);
        list = handler.getListAlarm();
        adapter = new RecyclerAdapter(this, list, new RecyclerAdapter.EventListener() {
            @Override
            public void onClickItem(Alarm alarm) {
                Intent intent = new Intent(MainActivity.this, DetailAlarm.class);
                intent.putExtra("mode", Const.MODE_EDIT);
                intent.putExtra("alarm", alarm);
                startActivityForResult(intent, Const.REQUEST_CODE_UPDATE_ALARM);
            }

            @Override
            public void onLongClick(final Alarm alarm) {
                builder.setMessage("delete?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.removeAlarm(alarm);
                        list.remove(alarm);
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Title Delete");
                alertDialog.show();
            }
        });

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        Intent serviceIntent = new Intent(this, AlarmService.class);
        alarmService = new AlarmService();
        mServiceIntent = new Intent(this, alarmService.getClass());
        if (!isMyServiceRunning(alarmService.getClass())) {
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    public void onClickButtonAdd(View v) {
        Intent intent = new Intent(MainActivity.this, DetailAlarm.class);
        intent.putExtra("mode", Const.MODE_ADD);
        startActivityForResult(intent, Const.REQUEST_CODE_ADD_ALARM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == DetailAlarm.RESULT_OK && requestCode == Const.REQUEST_CODE_ADD_ALARM) {
            Alarm alarm = (Alarm) data.getSerializableExtra("alarm");
            list.add(alarm);
            adapter.notifyDataSetChanged();
        }
        if (resultCode == DetailAlarm.RESULT_OK && requestCode == Const.REQUEST_CODE_UPDATE_ALARM) {
            int index = -1;
            Alarm alarm = (Alarm) data.getSerializableExtra("alarm");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(alarm.getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                list.set(index, alarm);
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}