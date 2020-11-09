package com.example.animation;

import android.graphics.Canvas;

/**
 * Created by Tinglan on 2020/10/11 00:21
 * It works!!
 */
public interface BaseAnimation {
    void draw(Canvas canvas);
    void release();
    void update(float animatorValue);
}
