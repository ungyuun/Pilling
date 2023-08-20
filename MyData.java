package com.pilling.app;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

public class MyData {
    private String filename;
    private String fileUrl;
    private String date;
    private String prescription;


    private Bitmap bitmap;


    public MyData(Bitmap bitmap, String prescription,String date) {
        this.bitmap = bitmap;
        this.prescription = prescription;
        this.date = date;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getFilename() {
        return filename;
    }
    public String getFileUrl() {
        return fileUrl;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "{" +
                "prescription=" + prescription + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}