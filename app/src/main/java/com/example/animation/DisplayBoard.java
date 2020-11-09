package com.example.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import com.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tinglan on 2020/10/10 19:25
 * It works!!
 */
public class DisplayBoard implements BaseAnimation {

    private Paint mPaint;
    private List<String> mText = new ArrayList<>();
    private int mWidth;
    private int mHeight;

    public DisplayBoard(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
        mPaint = new Paint();
        mPaint.setTextSize(Utils.dp2Px(26));
        // 画笔颜色黄色
        mPaint.setColor(Color.rgb(0xFF, 0xC1, 0x25));
        Typeface font = Typeface.create("宋体", Typeface.ITALIC);
        mPaint.setTypeface(font);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void addString(String string) {
        mText.add(string);
    }

    // 在屏幕中央绘制文字
    @Override
    public void draw(Canvas canvas) {
        int length = mText.size();
        if(length == 0)
            return;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        float total = (length - 1) * (bottom - top) + (fontMetrics.descent - fontMetrics.ascent);
        float offset = total / 2 - bottom;
        for (int i = 0; i < length; i++) {
            float yAxis = -(length - i - 1) * (-top + bottom) + offset;
            canvas.drawText(mText.get(i), mWidth / 2.0f, mHeight / 2.0f + yAxis, mPaint);
        }
    }

    @Override
    public void release() {
        mText.clear();
    }

    @Override
    public void update(float animatorValue) {

    }
}
