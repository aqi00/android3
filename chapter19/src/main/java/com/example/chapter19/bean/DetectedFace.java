package com.example.chapter19.bean;

import android.graphics.Bitmap;

public class DetectedFace {
    private Bitmap bitmap;
    private Float similarity;

    public DetectedFace(Bitmap bitmap, Float similarity) {
        this.bitmap = bitmap;
        this.similarity = similarity;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setSimilarity(Float similarity) {
        this.similarity = similarity;
    }

    public Float getSimilarity() {
        return this.similarity;
    }

}
