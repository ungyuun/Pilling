package com.pilling.kakaologin;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.appcompat.widget.SwitchCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private static List<AlarmData> dataList;
    private Context context;

    private OnItemClickListener listener;
    private SwitchCompat switchCompat;

    public AlarmAdapter(List<AlarmData> dataList, Context context, OnItemClickListener listener) {
        this.dataList = dataList;
        this.context = context;
        this.listener = listener;

    }
    public interface OnItemClickListener {
        void onItemClick(AlarmData item);


    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView timeView;
        SwitchCompat toggleButton;



        public ViewHolder(View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.alarm_time);
            toggleButton = itemView.findViewById(R.id.alarm_switch);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                AlarmData item = dataList.get(position);
                listener.onItemClick(item);
            }
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_alarm_recycler, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmData data = dataList.get(position);
        holder.timeView.setText(data.getHour()+" : "+data.getMin());
        holder.toggleButton.setChecked(data.getAct());  // ToggleButton 상태 설정
        holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // ToggleButton 상태 변경 시 처리할 로직 작성

                if (isChecked) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(data.getHour()));
                    c.set(Calendar.MINUTE, Integer.parseInt(data.getMin()));
                    c.set(Calendar.SECOND, 0);
                    updateAlarm(c,data.getRequestCode());

                } else {
                    cancelAlarm(data.getRequestCode());
                }
            }
        });

    }
    private void updateAlarm(Calendar c, Integer thisRequestCode){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("calendar", c.getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, thisRequestCode,
                intent, PendingIntent.FLAG_MUTABLE | 0);
        if(c.before((Calendar.getInstance()))){
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);

    }
    private void cancelAlarm(Integer thisRequestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, thisRequestCode, intent, PendingIntent.FLAG_MUTABLE | 0);
        Log.d("check","AlarmAdapter의 컨텍스트  : "+context);
        alarmManager.cancel(pendingIntent);
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}