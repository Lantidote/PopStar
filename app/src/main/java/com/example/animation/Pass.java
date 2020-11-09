package com.example.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import com.example.popstar.R;

/**
 * Created by Tinglan on 2020/10/7 16:28
 * It works!!
 */
public class Pass implements BaseAnimation {

    private Context mContext;
    private Paint mPaint;
    private Bitmap mBitmap;
    private float mLeft;
    private float mTop;
    private float mAnimationValue;

    public Pass(Context context) {
        mContext = context;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mTop = metrics.heightPixels * 0.3f;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.stage_clear);
        float scale = metrics.widthPixels * 0.3f / mBitmap.getWidth();
        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() * scale),
                (int) (mBitmap.getHeight() * scale), true);
        mLeft = (metrics.widthPixels - mBitmap.getWidth()) / 2.0f;
        mPaint = new Paint();
        mAnimationValue = 1.0f;
    }

    @Override
    public void update(float animatedValue) {
        mAnimationValue = animatedValue;
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setAlpha((int) (255 * mAnimationValue));
        if (mBitmap != null && !mBitmap.isRecycled())
            canvas.drawBitmap(mBitmap, mLeft, mTop, mPaint);
    }

    @Override
    public void release() {
        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
    }
}
