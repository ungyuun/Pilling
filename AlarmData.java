package com.pilling.app;



import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmData {
    private final String kakaoId;
    private final String hour;
    private final String min;
    private int requestCode;
    private boolean act;


    public AlarmData(String kakaoId,String hour,String min,Integer requestCode,Boolean act) {
        this.kakaoId=kakaoId;
        this.hour = hour;
        this.min =min;
        this.requestCode = requestCode;
        this.act = act;

    }
    public void setRC(Integer requestCode){
        this.requestCode = requestCode;
    }

    public void setAct(boolean act) {
        this.act = act;
    }

    public String getKakaoId(){return kakaoId;}
    public String getHour(){return hour;}
    public String getMin(){return min;}
    public Integer getRequestCode(){return requestCode;}
    public Boolean getAct() {return act;}

    public Integer getRC(){return requestCode;}
}