package com.pilling.app;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class AlarmCall extends AppCompatActivity {
    private String kakaoId;
    Ringtone rt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_call);
        Intent intent = getIntent();
        kakaoId = intent.getStringExtra("kakaoId");

        Button button_finish = findViewById(R.id.button_finish);

        TextView timefloat = findViewById(R.id.timefloat);

        this.kakaoId = intent.getStringExtra("kakaoId");



        long timeInMillis = intent.getLongExtra("calendar", 0);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        timefloat.setText(String.format("%02d:%02d",hour,minute));

        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);



        button_finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertReceiver.rt.stop();

                finish();
            }
        });

//
//
//        button_cancel.setOnClickListener(new View.OnClickListener() {                 // 취소버튼인데 누르면 항목에서 미동작으로 구현  그러기 위해서 requestCode가 몇인지 파악해야함
//            @Override
//            public void onClick(View view) {
//                Intent getDrugInfo = new Intent(AlarmActivity.this,RestGet.class);     // restapi 연습 예제
//
//                getDrugInfo.putExtra("kakaoId",kakaoId);
//                startActivity(getDrugInfo);
//            }
//        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
}