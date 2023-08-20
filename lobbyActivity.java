package com.pilling.app;




import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pilling.app.GetPrescription;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class lobbyActivity extends AppCompatActivity{

    String KakaoId;
    TextView testView;
    public static final String IMAGE_URL = "https://23e2-218-159-71-13.ngrok-free.app/"; // 플라스크 서버의 이미지 URL로 대체해야 합니다.
    private ImageView imageView;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<MyData> dataList;
    private ImageButton imageButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Intent intent = getIntent();
        KakaoId = intent.getStringExtra("kakaoId");

        imageView = findViewById(R.id.getView);                                 // 이미지뷰 값 가져옴


        imageButtons = findViewById(R.id.alarmPage);


        Intent getPrescrtionInfo = new Intent(lobbyActivity.this,RestGet.class);     // restapi 연습 예제

        getPrescrtionInfo.putExtra("kakaoId",KakaoId);
        startActivity(getPrescrtionInfo);
//        GetPrescription getPrescription = new GetPrescription(this);
//        getPrescription.execute();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class FABClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(lobbyActivity.this, TakePicActivity.class);         // 사진 파비콘

            intent.putExtra("kakaoId", KakaoId);
            startActivity(intent);
        }
    }

    public void alarmPageClick(View view) {

    }
}
