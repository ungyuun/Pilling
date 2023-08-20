package com.pilling.app;

import android.app.Application;


import com.kakao.sdk.common.KakaoSdk;

public class Pilling extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        KakaoSdk.init(this,"c7e11030e8b06d71b29feca570e73d42");
    }

}