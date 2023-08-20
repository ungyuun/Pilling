package com.pilling.app;





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
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SettingActivity extends AppCompatActivity{
    private String kakaoId;
    private ImageButton alarmPage;
    private ImageButton drugPage;
    private Button logout;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        alarmPage = findViewById(R.id.alarmPage);
        drugPage = findViewById(R.id.drugPage);
        logout = findViewById(R.id.logout);



        alarmPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getAlarmInfo = new Intent(SettingActivity.this,RestGet.class);     // restapi 연습 예제

                getAlarmInfo.putExtra("kakaoId",kakaoId);
                startActivity(getAlarmInfo);
            }
        });
        drugPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getDrugInfo = new Intent(SettingActivity.this,RestGet.class);     // restapi 연습 예제

                getDrugInfo.putExtra("kakaoId",kakaoId);
                startActivity(getDrugInfo);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        // 로그아웃 성공 또는 실패에 따라 처리할 작업을 여기에 작성
                        if (throwable != null) {
                            // 로그아웃 실패 처리
                            Log.e(TAG, "로그아웃 실패: " + throwable.getMessage());
                        } else {
                            // 로그아웃 성공 처리
                            Log.d(TAG, "로그아웃 성공");
                        }
                        Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                        finishAffinity();
                        startActivity(intent);
                        return null;
                    }
                });
            }
        });
    }
    private void kakaoLogout() {

    }


}
