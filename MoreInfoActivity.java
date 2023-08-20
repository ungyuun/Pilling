package com.pilling.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoreInfoActivity extends AppCompatActivity implements MedecineAdapter.OnItemClickListener{


    private String kakaoId;
    private String prescription;

    private RecyclerView recyclerView;
    static List<MedecineData> dataList;
    private MedecineAdapter medecineadapter;
    Retrofit retrofit;
    MyApi myApi;
    Call<List<MedecineData>> call;
    private ImageButton imageButtons;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        imageButtons = findViewById(R.id.alarmPage);
        Intent intent = getIntent();
        this.prescription = intent.getStringExtra("prescription");
        String baseurl = intent.getStringExtra("baseurl");



        recyclerView = findViewById(R.id.druglist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)    //베이스 url등록
                .addConverterFactory(GsonConverterFactory.create())    //JSON -> 자바객채변환
                .build();
        System.out.println("prescciprpi: "+prescription);
        myApi = retrofit.create(MyApi.class);
        call = myApi.getMedecineData(prescription);                        //https://cjw-awdsd.tistory.com/16 restapi 참조게시물
        //API 호출
        call.enqueue(new Callback<List<MedecineData>>() {    //비동기로 실행되어 콜백으로 앱으로 알려줌
            @Override
            public void onResponse(Call<List<MedecineData>> call, Response<List<MedecineData>> response) {

                List<MedecineData> medecineDataList = response.body();
                dataList = new ArrayList<>();
                System.out.println("데이터리스트 : "+dataList);
                for (MedecineData medecineData : medecineDataList) {                              // 요청으로 반복문에 파일이름을 넣어서 받아옴
//                    // 각각의 MyData 객체에 대한 처리
                    String medecine = medecineData.getMedecine();
                    String thumbnail = medecineData.getThumbnail();
                    String link = medecineData.getLink();
                    String efficacy = medecineData.getEfficacy();
                    String shape = medecineData.getShape();
                    String color1 = medecineData.getColor1();
                    String color2 = medecineData.getColor2();
                    dataList.add(new MedecineData(medecine,thumbnail,link,efficacy,shape,color1,color2));
                    System.out.println("medecine :"+medecine);

                }
                System.out.println("데이터리스트 : "+dataList);
                medecineadapter = new MedecineAdapter(dataList,MoreInfoActivity.this);
                recyclerView.setAdapter(medecineadapter);

            }
            @Override
            public void onFailure(Call<List<MedecineData>> call, Throwable t) {
                t.printStackTrace();
            }
        });



        imageButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getAlarmInfo = new Intent(MoreInfoActivity.this,AlarmActivity.class);     // restapi 연습 예제

                getAlarmInfo.putExtra("kakaoId",kakaoId);
                startActivity(getAlarmInfo);
            }
        });
    }

    @Override
    public void onItemClick(MedecineData item) {
        String url = item.getLink();

        Intent linkUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(url));     // restapi 연습 예제


        startActivity(linkUrl);
    }
    public Context getContext() {
        return this; // 액티비티 자체가 컨텍스트를 가지고 있으므로, 액티비티 인스턴스를 반환합니다.
    }


}
