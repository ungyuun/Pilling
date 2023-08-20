package com.pilling.kakaologin;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface MyApi {

    @Multipart
    @POST("main/post/")
    Call<ResponseBody> post_posts(
            @Part("title") RequestBody param,
            @Part MultipartBody.Part image
    );

    @POST("alarm")
    Call<ResponseBody> postAlarm(@Body AlarmData alarmData);

    @POST("requestcode")
    Call<ResponseBody> postRequestCode(@Body Integer requestCode);

    @POST("prescription")
    Call<ResponseBody> postPrescription(@Body String requestCode);

    @GET("alarm/{kakaoId}")
    Call<List<AlarmData>> getAlarm(@Path("kakaoId") String kakaoId);

    @GET("alarm/count/{kakaoId}")
    Call<AlarmData> getIntegerValue(@Path("kakaoId") String kakaoId);

    @GET("imagedata/{kakaoId}")
    Call<List<MyData>> getImageData(@Path("kakaoId") String kakaoId);

    @GET("getimage/{prescription}")
    Call<List<MyData>> getImage(@Path("prescription") String prescription);

    @GET("druginfo/{prescription}")
    Call<List<MedecineData>> getMedecineData(@Path("prescription") String prescription);

}