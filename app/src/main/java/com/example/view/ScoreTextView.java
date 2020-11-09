package com.example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.popstar.R;

/**
 * Created by Tinglan on 2020/9/22 11:29
 * It works!!
 */
public class ScoreTextView extends RelativeLayout {

    private static final String TAG = "ScoreTextView";
    private int mScore = 0;
    private TextView mTitleTv = null;
    private TextView mScoreTv = null;

    public ScoreTextView(Context context) {
        this(context, null);
    }

    public ScoreTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        // RelativeLayout嵌套score_text_view
        LayoutInflater.from(context).inflate(R.layout.score_text_view, this);
        mTitleTv = this.findViewById(R.id.tv_title);
        mScoreTv = this.findViewById(R.id.tv_score);
    }

    public int getScore() {
        return mScore;
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setScore(int score) {
        mScore = score;
        updateScore(score);
    }

    private void updateScore(int score) {
        mScoreTv.setText(String.valueOf(score));
    }

    // 单位dp
    public void setScoreLayoutWidth(int width) {
        ViewGroup.LayoutParams layoutParams = mScoreTv.getLayoutParams();
        // 由context获取屏幕宽度等信息 width的默认单位为像素 此处由dp单位转换为像素单位
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        layoutParams.width = width * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
//        LogUtil.d(TAG, "metrics.densityDpi ==> " + metrics.densityDpi
//        + " DisplayMetrics.DENSITY_DEFAULT ==> " + DisplayMetrics.DENSITY_DEFAULT);
    }
}
