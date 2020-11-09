package com.example.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import com.example.constants.Constants;
import com.example.popstar.R;

/**
 * Created by Tinglan on 2020/10/24 11:17
 * It works!!
 */
public class GoodScore implements BaseAnimation {

    private static final String TAG = GoodScore.class.getSimpleName();

    private Context mContext;
    private Paint mPaint;
    private Bitmap mBitmap;
    private float mTop;
    private float mAnimationValue;

    public GoodScore(Context context, int mode) {
        mContext = context;
        initBitmap(mode);
        mPaint = new Paint();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mTop = metrics.heightPixels / 7.0f * 2;
        mAnimationValue = 1.0f;
    }

    private void initBitmap(int mode) {
        switch (mode) {
            case Constants.COOL:
                mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.combo_cool);
                break;
            case Constants.AWESOME:
                mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.combo_awesome);
                break;
            case Constants.FANTASTIC:
                mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.combo_fantastic);
                break;
        }
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float scale = (float) metrics.widthPixels / mBitmap.getWidth();
        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() * scale), (int) (mBitmap.getHeight() * scale), true);
    }

    @Override
    public void update(float animatedValue) {
        mAnimationValue = animatedValue;
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setAlpha((int) (255 * mAnimationValue));
        if (mBitmap != null && !mBitmap.isRecycled())
            canvas.drawBitmap(mBitmap, 0, mTop, mPaint);
    }

    @Override
    public void release() {
        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
    }
}
