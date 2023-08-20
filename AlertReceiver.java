package com.pilling.kakaologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {

    static Ringtone rt;
    int hour;
    int minute;
    private String kakaoId;


    @Override
    public void onReceive(Context context, Intent intent) {
        kakaoId = intent.getStringExtra("kakaoId");

        long timeInMillis = intent.getLongExtra("calendar", 0);
        System.out.println("알레트리시버에서 : "+timeInMillis);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        Intent alarmCall = new Intent(context, AlarmCall.class);
        alarmCall.putExtra("calendar", c.getTimeInMillis());
        alarmCall.putExtra("kakaoId", kakaoId);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        rt = RingtoneManager.getRingtone(context.getApplicationContext(), notification);

        alarmCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rt.play();
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500); // 0.5초간 진동
        context.startActivity(alarmCall);



        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1,nb.build());
    }
}