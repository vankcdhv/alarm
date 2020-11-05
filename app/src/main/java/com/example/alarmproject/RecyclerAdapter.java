package com.example.alarmproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Alarm> list;
    Context context;
    private DatabaseHandler databaseHandler;
    private EventListener eventListener;

    public RecyclerAdapter(Context context, ArrayList<Alarm> list, EventListener eventListener) {
        this.context = context;
        this.list = list;
        this.eventListener = eventListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.cardview_alarm, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(list.get(i).getTitle());
        viewHolder.itemTime.setText(list.get(i).getTime());
        viewHolder.itemDate.setText(list.get(i).getDate());
        viewHolder.btnSwitch.setChecked(list.get(i).isActive());
        final int index = i;
        viewHolder.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHandler = new DatabaseHandler(context, null, null, 1);
                Alarm alarm = list.get(index);
                alarm.setActive(!alarm.isActive());
                int result = databaseHandler.updateAlarm(alarm);
                if (result == -1) {
                    Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show();
                    viewHolder.btnSwitch.setChecked(!alarm.isActive());
                } else {
                    Toast.makeText(context, "Alarm " + alarm.getTime() + " is " + (alarm.isActive() ? "active" : "deactive"), Toast.LENGTH_SHORT).show();
                    list.set(index, alarm);
                }
            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventListener.onClickItem(list.get(index));
            }
        });
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                eventListener.onLongClick(list.get(index));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle, itemTime, itemDate;
        public Switch btnSwitch;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemTime = itemView.findViewById(R.id.itemTime);
            itemDate = itemView.findViewById(R.id.itemDate);
            btnSwitch = itemView.findViewById(R.id.btnSwitch);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public interface EventListener {
        public void onClickItem(Alarm alarm);

        public void onLongClick(Alarm alarm);
    }
}
