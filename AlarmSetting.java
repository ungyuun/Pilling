package com.pilling.kakaologin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlarmSetting extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private TextView mTextView;

    private String kakaoId;
    private Integer maxRequestCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);
        Intent intent = getIntent();
        kakaoId = intent.getStringExtra("kakaoId");
        maxRequestCode = intent.getIntExtra("maxRequestCode",1);
        mTextView =  findViewById(R.id.textView);

        this.maxRequestCode = ++maxRequestCode;
        Button button = (Button) findViewById(R.id.button_timepicker);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }

        });

//        Button buttonCancelAlarm = findViewById(R.id.button_cancel);
//        buttonCancelAlarm.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick (View v) {
//                cancelAlarm();
//            }
//        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);

        startAlarm(c);
    }

    private void updateTimeText(Calendar c){
        String timeText = "Alarm set for : ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        mTextView.setText(timeText);
    }

    private void startAlarm(Calendar c){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(lobbyActivity.IMAGE_URL) // API의 기본 URL 설정
                .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 설정
                .build();

        MyApi myApi = retrofit.create(MyApi.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("calendar", c.getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, maxRequestCode,
                intent, PendingIntent.FLAG_MUTABLE | 0);
        String formattedMinute = String.format("%02d", c.get(Calendar.MINUTE));
        Log.d("AlarmSetting","AlarmSetting : "+pendingIntent);
        AlarmData requestData = new AlarmData(kakaoId,String.valueOf(c.get(Calendar.HOUR_OF_DAY)),formattedMinute,maxRequestCode,true);

        Call<ResponseBody> call = myApi.postAlarm(requestData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println("디비저장 성공");
                } else {
                    System.out.println("디비저장 실패");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("알람저장중 타 오류"+t.getMessage());
            }
        });
        if(c.before((Calendar.getInstance()))){
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        finish();

    }

}