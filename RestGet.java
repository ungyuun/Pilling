package com.pilling.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestGet extends AppCompatActivity implements MyAdapter.OnItemClickListener{
    // 네트워크 요청을 수행할 백그라운드 스레드

    TextView textView;
    Retrofit retrofit;
    MyApi myApi;
    Call<List<MyData>> call;

    public static String kakaoId;
    private ImageView imageView;
    static List<MyData> dataList;
    private MyAdapter adapter;
    private ImageButton imageButton;
    private FloatingActionButton fabmain;
    private ProgressBar progressBar;

    private int asyncCount = 0;

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        imageView = findViewById(R.id.getView);
        Button setting = findViewById(R.id.setting);             //임시
        recyclerView = findViewById(R.id.recyclerView);
        imageButton = findViewById(R.id.alarmPage);
        progressBar = findViewById(R.id.progressBar);
        Intent intent = getIntent();

        this.kakaoId = intent.getStringExtra("kakaoId");
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = getIntent();
                finish(); //현재 액티비티 종료 실시
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                startActivity(intent); //현재 액티비티 재실행 실시
                overridePendingTransition(0, 0);
            }
        });

        fabmain = findViewById(R.id.fabMain);


        fabmain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestGet.this, TakePicActivity.class);         // 사진 파비콘

                intent.putExtra("kakaoId", kakaoId);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestGet.this, SettingActivity.class);         // 사진 파비콘

                intent.putExtra("kakaoId", kakaoId);
                startActivity(intent);
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getAlarmInfo = new Intent(RestGet.this,AlarmActivity.class);     // restapi 연습 예제

                getAlarmInfo.putExtra("kakaoId",kakaoId);
                startActivity(getAlarmInfo);
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(lobbyActivity.IMAGE_URL)    //베이스 url등록
                .addConverterFactory(GsonConverterFactory.create())    //JSON -> 자바객채변환
                .build();

        myApi = retrofit.create(MyApi.class);
        call = myApi.getImageData(this.kakaoId);                        //https://cjw-awdsd.tistory.com/16 restapi 참조게시물
        //API 호출
        call.enqueue(new Callback<List<MyData>>() {    //비동기로 실행되어 콜백으로 앱으로 알려줌
            @Override
            public void onResponse(Call<List<MyData>> call, Response<List<MyData>> response) {

                List<MyData> myDataList = response.body();

                dataList = new ArrayList<>();

                for (MyData myData : myDataList) {                              // 요청으로 반복문에 파일이름을 넣어서 받아옴
//                    // 각각의 MyData 객체에 대한 처리

                    String prescription = myData.getPrescription();
                    String date = myData.getDate();
                    System.out.println("date : "+date);
                    GetPrescription getPrescription = new GetPrescription(imageView,progressBar,prescription,date);

                    getPrescription.execute(lobbyActivity.IMAGE_URL+"downloadImage/"+prescription);
                    getPrescription.setCallback(new GetPrescription.Callback() {
                        @Override
                        public void onTaskComplete() {
                            asyncCount++;  // 변수 증가
                            System.out.println("앤씨카운트 ++");
                            if (asyncCount == myDataList.size()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                adapter = new MyAdapter(dataList, getContext(),RestGet.this);
                                recyclerView.setAdapter(adapter);


                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<MyData>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
        public Context getContext() {
            return this; // 액티비티 자체가 컨텍스트를 가지고 있으므로, 액티비티 인스턴스를 반환합니다.
        }

    @Override
    public void onItemClick(MyData item) {                      //해당하는 처방전 상세조회 시작

        Intent moreInfo = new Intent(RestGet.this,MoreInfoActivity.class);     // restapi 연습 예제

        moreInfo.putExtra("kakaoId",getKakaoId());
        moreInfo.putExtra("prescription",item.getPrescription());
        moreInfo.putExtra("baseurl",lobbyActivity.IMAGE_URL);
        startActivity(moreInfo);
    }
    public String getKakaoId(){
            return kakaoId;
    }

}