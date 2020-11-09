package com.example.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.example.animation.DisplayBoard;
import com.example.animation.Firework;
import com.example.animation.GoodScore;
import com.example.animation.Pass;
import com.example.constants.Constants;
import com.example.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Tinglan on 2020/10/6 22:12
 * It works!!
 */
public class AnimationView extends View {

    private static final String TAG = "AnimationView";

    private ArrayList<Firework> mFireworks;
    private Pass mPass = null;
    private DisplayBoard mDisplayBoard;
    private ArrayList<GoodScore> mGoodScores;

    private static OnAnimationViewListener sOnAnimationViewListener = null;

    // 如果View是在Java代码里面new的，则调用第一个构造函数
    // 如果View是在.xml里声明的，则调用第二个构造函数 自定义属性是从AttributeSet参数传进来的
    public AnimationView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mFireworks = new ArrayList<>();
        mGoodScores = new ArrayList<>();
        mDisplayBoard = new DisplayBoard(context);
    }

    public void addAnimation(int type, String... value) {
        switch (type) {
            case Constants.CONGRATULATIONS_ON_PASS: {
                if (mPass == null) {
                    mPass = new Pass(getContext());
                    // 播放恭喜通关动画
                    if (sOnAnimationViewListener != null) {
                        sOnAnimationViewListener.onPass();
                    }
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                    valueAnimator.setRepeatCount(4);
                    valueAnimator.setRepeatMode(ValueAnimator.RESTART);
                    valueAnimator.setDuration(300);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mPass.update((Float) animation.getAnimatedValue());
                            invalidate();
                        }
                    });
                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mPass.update(1.0f);
                        }
                    });
                    valueAnimator.start();
                    invalidate();
                }
                break;
            }
            case Constants.DISPLAY_PASS_BONUS: {
                LogUtil.d(TAG, "显示奖励分和剩余星星个数");
                int intValue = Integer.parseInt(value[0]);
                mDisplayBoard.addString("奖励 " + (intValue < 10 ? 2000 - 20 * intValue * intValue : 0));
                mDisplayBoard.addString("剩余 " + value[0] + " 颗星");
                invalidate();
                break;
            }
            case Constants.COOL: {
                displayScoreAnimation(new GoodScore(getContext(), Constants.COOL));
                break;
            }
            case Constants.AWESOME: {
                displayScoreAnimation(new GoodScore(getContext(), Constants.AWESOME));
                break;
            }
            case Constants.FANTASTIC: {
                displayScoreAnimation(new GoodScore(getContext(), Constants.FANTASTIC));
                break;
            }
        }
    }

    public void displayScoreAnimation(final GoodScore goodScore) {
        mGoodScores.add(goodScore);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setRepeatCount(2);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                goodScore.update((Float) animation.getAnimatedValue());
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mGoodScores.remove(goodScore);
                goodScore.release();
            }
        });
        valueAnimator.start();
        invalidate();
    }

    public static void setOnAnimationViewListener(OnAnimationViewListener onAnimationViewListener) {
        sOnAnimationViewListener = onAnimationViewListener;
    }

    interface OnAnimationViewListener {
        void onPass();
    }

    public void addFirework(Firework firework) {
//        LogUtil.d(TAG,"width ==> "+ getWidth() + " height ==> " + getHeight());
        mFireworks.add(firework);
        explode(firework);
    }

    private void explode(final Firework firework) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.setDuration(firework.getDuration());
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                firework.update((Float) animation.getAnimatedValue());
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFireworks.remove(firework);
                firework.release();
            }
        });
        valueAnimator.start();
        invalidate();
    }

    // 释放持有资源
    public void release() {

        int length = mFireworks.size();
        for (int i = length - 1; i >= 0; i--) {
            Firework firework = mFireworks.get(i);
            mFireworks.remove(firework);
            firework.release();
        }

        if (mPass != null) {
            mPass.update(0);
            // 重画使其变透明
            invalidate();
            mPass.release();
            mPass = null;
        }

        mDisplayBoard.release();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogUtil.d(TAG, "onDraw");
        super.onDraw(canvas);
        for (Firework firework : mFireworks) {
            firework.draw(canvas);
        }
        if (mPass != null) {
            mPass.draw(canvas);
        }
        for (GoodScore goodScore : mGoodScores) {
            goodScore.draw(canvas);
        }
        mDisplayBoard.draw(canvas);
    }

}
