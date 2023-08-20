package com.pilling.kakaologin;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AlarmActivity extends AppCompatActivity implements AlarmAdapter.OnItemClickListener, TimePickerDialog.OnTimeSetListener{
    private String kakaoId;
    private ImageButton imageButton;
    private FloatingActionButton fabmain;
    private Integer maxRequestCode;
    Retrofit retrofit;
    Call<List<AlarmData>> call;
    static List<AlarmData> dataList;
    private AlarmAdapter alarmadapter;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private TextView mTextView;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Intent intent = getIntent();
        kakaoId = intent.getStringExtra("kakaoId");
        imageButton = findViewById(R.id.drugPage);
        fabmain = findViewById(R.id.fabMain);
        recyclerView = findViewById(R.id.recyclerView);

        retrofit = new Retrofit.Builder()
                .baseUrl(lobbyActivity.IMAGE_URL)    //베이스 url등록
                .addConverterFactory(GsonConverterFactory.create())    //JSON -> 자바객채변환
                .build();
        MyApi myApi = retrofit.create(MyApi.class);
        call = myApi.getAlarm(RestGet.kakaoId);                        //https://cjw-awdsd.tistory.com/16 restapi 참조게시물
        //API 호출
        call.enqueue(new Callback<List<AlarmData>>() {    //비동기로 실행되어 콜백으로 앱으로 알려줌

            @Override
            public void onResponse(Call<List<AlarmData>> call, Response<List<AlarmData>> response) {

                List<AlarmData> alarmDataList = response.body();
                dataList = new ArrayList<>();

                for (AlarmData alarmData : alarmDataList) {                              // 요청으로 반복문에 파일이름을 넣어서 받아옴
//                    // 각각의 MyData 객체에 대한 처리
                    String hour = alarmData.getHour();
                    String min = alarmData.getMin();
                    Integer requestCode = alarmData.getRequestCode();
                    Boolean act = alarmData.getAct();
                    dataList.add(new AlarmData(kakaoId,hour,min,requestCode,act));
                    System.out.println("하워 민 리쿼세 : "+hour+min+requestCode+act);

                }

                alarmadapter = new AlarmAdapter(dataList, getContext(),AlarmActivity.this);
                recyclerView.setAdapter(alarmadapter);

            }
            @Override
            public void onFailure(Call<List<AlarmData>> call, Throwable t) {
                Log.d("error","통신 실패: " + t.getMessage());
            }
        });
        Call<AlarmData> call = myApi.getIntegerValue(kakaoId);
        call.enqueue(new Callback<AlarmData>() {
            @Override
            public void onResponse(Call<AlarmData> call, Response<AlarmData> response) {
                if (response.isSuccessful()) {
                    AlarmData alarmData = response.body();
                    if (alarmData != null){
                        Integer requestCode = alarmData.getRequestCode();
                        AlarmActivity.this.maxRequestCode = ++requestCode;

                    }
                } else {
                }
            }
            @Override
            public void onFailure(Call<AlarmData> call, Throwable t) {
                Log.d("error","잘못됨"+t.getMessage());
            }
        });
        fabmain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog = new Dialog(AlarmActivity.this,R.style.Dialog);
                dialog.setContentView(R.layout.modal_layout);
                Button button = (Button)dialog.findViewById(R.id.button_timepicker);
                mTextView =  findViewById(R.id.textView);
                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        DialogFragment timePicker = new TimePickerFragment();
                        timePicker.show(getSupportFragmentManager(), "time picker");
                    }

                });
                dialog.show();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getDrugInfo = new Intent(AlarmActivity.this,RestGet.class);     // restapi 연습 예제

                getDrugInfo.putExtra("kakaoId",kakaoId);
                startActivity(getDrugInfo);
            }
        });

    }

    @Override
    public void onItemClick(AlarmData item) {
        Integer requestCode = item.getRequestCode();
        dialog = new Dialog(AlarmActivity.this,R.style.Dialog);
        dialog.setContentView(R.layout.alarm_edit_modal);
        Button add = (Button)dialog.findViewById(R.id.button_timepicker);
        Button delete = (Button)dialog.findViewById(R.id.button_delete);
        mTextView =  dialog.findViewById(R.id.textView);
        mTextView.setText(item.getHour()+" : "+item.getMin());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(lobbyActivity.IMAGE_URL) // API의 기본 URL 설정
                .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 설정
                .build();

        MyApi myApi = retrofit.create(MyApi.class);

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(AlarmActivity.this, AlertReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,requestCode, intent, PendingIntent.FLAG_MUTABLE | 0);
                alarmManager.cancel(pendingIntent);

                Call<ResponseBody> call = myApi.postRequestCode(requestCode);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                        } else {
                            System.out.println("디비저장 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println("알람저장중 타 오류"+t.getMessage());
                    }
                });

                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(AlarmActivity.this, AlertReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,requestCode, intent, PendingIntent.FLAG_MUTABLE | 0);
                alarmManager.cancel(pendingIntent);

                Call<ResponseBody> call = myApi.postRequestCode(requestCode);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Intent intent = getIntent();
                            finish(); //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                            startActivity(intent); //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0);
                            dialog.dismiss();
                        } else {
                            System.out.println("디비저장 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println("알람저장중 타 오류"+t.getMessage());
                    }
                });
            }
        });
        dialog.show();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

//        updateTimeText(c);

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

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("calendar", c.getTimeInMillis());
        pendingIntent = PendingIntent.getBroadcast(this, maxRequestCode,
                intent, PendingIntent.FLAG_MUTABLE | 0);
        String formattedMinute = String.format("%02d", c.get(Calendar.MINUTE));
        Log.d("check","AlarmActivity의 컨텍스트  : "+this);
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
        intent = getIntent();
        finish(); //현재 액티비티 종료 실시
        overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
        startActivity(intent); //현재 액티비티 재실행 실시
        overridePendingTransition(0, 0);
        dialog.dismiss();
    }

    public void cancelAlarm(Integer thisRequestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, thisRequestCode, intent, PendingIntent.FLAG_MUTABLE | 0);
        alarmManager.cancel(pendingIntent);
    }


    public Context getContext() {
        return this; // 액티비티 자체가 컨텍스트를 가지고 있으므로, 액티비티 인스턴스를 반환합니다.
    }

}
