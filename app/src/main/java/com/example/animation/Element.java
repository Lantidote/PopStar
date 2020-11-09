package com.example.animation;

import android.graphics.Bitmap;

/**
 * Created by Tinglan on 2020/9/16 22:36
 * It works!!
 */
// Element代表一个烟花粒
public class Element {

    public float mElementX = 0;
    public float mElementY = 0;
    public int mColor;
    public double mDirection;
    public float mSpeed;
    public Bitmap mBitmap;

    public Element(int color, double direction, float speed, Bitmap bitmap){
        mColor = color;
        mDirection = direction;
        mSpeed = speed;
        mBitmap = bitmap;
    }
}
