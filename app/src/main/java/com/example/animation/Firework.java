package com.example.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.popstar.R;
import com.example.utils.LogUtil;
import com.example.utils.Utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tinglan on 2020/9/16 21:42
 * It works!!
 */

public class Firework implements BaseAnimation{

    private static final String TAG = "Firework";
    private static final int bitmapColor[] = { R.drawable.light_blue,
            R.drawable.light_yellow, R.drawable.light_green,
            R.drawable.light_purple, R.drawable.light_red };

    private Context mContext;
    private int x;
    private int y;
    private int mColor;

    private float mGravity;
    private int mCount; // count of element
    private float mLaunchSpeed; // 发射速度
    private int mDuration;  // 持续时间

    private Paint mPaint = new Paint();
    private float mAnimatorValue = 1.0f;
    private ArrayList<Element> mElements = new ArrayList<>();
    private Random mRandom = new Random(System.currentTimeMillis());

    public Firework(Context context, int x, int y, int color) {
        mContext = context;
        this.x = x;
        this.y = y;
        mColor = color;
    }

    public void setGravity(float gravity) {
        mGravity = gravity;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public void setLaunchSpeed(float launchSpeed) {
        mLaunchSpeed = launchSpeed;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    public void initBombFirework() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        // 给每个火花设定一个随机的方向 0-360
        mElements.clear();
        for (int i = 0; i < mCount; i++) {
            // 生成指定颜色的烟花
            InputStream inputStream = mContext.getResources().openRawResource(mColor);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            LogUtil.d(TAG,"densityDpi ==> " + metrics.densityDpi + " density ==> "+ metrics.density);
            // 生成星形固定大小位图
            Bitmap shapeBitmap = Utils.drawShapeBitmap(bitmap,
                    (int) (10 * metrics.density),
                    "star");
            Element element = new Element(mColor, Math.toRadians(mRandom.nextInt(360)),
                    mRandom.nextFloat() * mLaunchSpeed, shapeBitmap);
//            LogUtil.d(TAG, "Element direction ==> " + element.mDirection + " speed ==> " + element.mSpeed);
            mElements.add(element);
        }
    }

    public void initStartFirework() {
        for (int i = 0; i < mCount; i++) {
            mColor = bitmapColor[mRandom.nextInt(bitmapColor.length)];
            InputStream inputStream = mContext.getResources().openRawResource(mColor);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            mElements.add(new Element(mColor,Math.toRadians(mRandom.nextInt(360)),mRandom.nextFloat()
            *mLaunchSpeed,bitmap));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setAlpha((int) (255 * mAnimatorValue));
        for (Element element : mElements) {
            if (!element.mBitmap.isRecycled()) {
                canvas.drawBitmap(element.mBitmap, x + element.mElementX, y
                        + element.mElementY, mPaint);
            }
        }
    }

    @Override
    public void release() {
        for (Element element : mElements) {
            if (element.mBitmap != null && !element.mBitmap.isRecycled())
                element.mBitmap.recycle();
        }
    }

    @Override
    public void update(float animatorValue) {
        mAnimatorValue = animatorValue;
        for (Element element : mElements) {
            element.mElementX = (float) (element.mElementX + Math.cos(element.mDirection) *
                    element.mSpeed * mAnimatorValue);
            element.mElementY = (float) (element.mElementY - Math.sin(element.mDirection)
                    * element.mSpeed * mAnimatorValue + mGravity * (1 - mAnimatorValue));
        }
    }
}

