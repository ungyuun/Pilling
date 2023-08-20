package com.pilling.kakaologin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetPrescription extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    private String prescription;
    private String date;
    private List<MyData> dataList;
    private Callback callback;
    private ProgressBar progressBar;


    public GetPrescription(ImageView imageView,ProgressBar progressBar,String prescription,String date) {
        this.imageView = imageView;
        this.prescription = prescription;
        this.date = date;
        this.progressBar = progressBar;
    }
    public interface Callback {
        void onTaskComplete(); // 비동기 요청이 완료되었을 때 호출될 콜백 메서드
    }
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);

    }
    @Override
    protected Bitmap doInBackground(String... urls) {
        String imageUrl = urls[0];
        Bitmap bitmap = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(imageUrl)
                    .build();
            Response response = client.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {
            RestGet.dataList.add(new MyData(bitmap,prescription, date));
        }
        if (callback != null) {
            callback.onTaskComplete(); // 콜백 메서드 호출
        }
    }
}