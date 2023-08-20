package com.pilling.kakaologin;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

public class MedecineData {
    private final String medecine;
    private final String thumbnail;
    private final String link;
    private final String efficacy;
    private final String shape;
    private final String color1;
    private final String color2;


    public MedecineData(String medecine,String thumbnail,String link,String efficacy, String shape,String color1,String color2) {
        this.medecine = medecine;
        this.thumbnail = thumbnail;
        this.link = link;
        this.efficacy = efficacy;
        this.shape = shape;
        this.color1=color1;
        this.color2=color2;
    }


    public String getMedecine() {
        return medecine;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public String getLink() {
        return link;
    }

    public String getEfficacy() {
        return efficacy;
    }

    public String getShape() {
        return shape;
    }
    public String getColor1() { return color1; }
    public String getColor2() {
        return color2;
    }


}