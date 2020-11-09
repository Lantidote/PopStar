package com.example.view;

import android.content.Context;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.example.constants.Constants;
import com.example.popstar.R;
import com.example.repository.Repository;
import com.example.sounds.GameSoundPool;
import com.example.utils.LogUtil;


/**
 * Created by Tinglan on 2020/9/16 21:29
 * It works!!
 */
public class StartView extends RelativeLayout {

    private static final String TAG = "StartView";

    private final GameSoundPool mGameSoundPool;
    private ScoreTextView mHighestScoreTextView;
    private boolean mSound = true; // 设置声音是否开启
    private ImageView mSoundView;
    private Button mStartButton;
    private Button mResumeButton;
    private Button mAboutGameButton;
    private Handler mHandler;

    public StartView(Context context, GameSoundPool gameSoundPool, Handler handler) {
        this(context, null, gameSoundPool, handler);
    }

    public StartView(Context context, @Nullable AttributeSet attrs, GameSoundPool gameSoundPool, Handler handler) {
        this(context, attrs, 0, gameSoundPool, handler);
    }

    public StartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, GameSoundPool gameSoundPool, Handler handler) {
        super(context, attrs, defStyleAttr);
        mGameSoundPool = gameSoundPool;
        mHandler = handler;
        initView(context);
        // 在此设置各个按键的监听
        initClick();
        // load是异步完成的 因此需要加载完毕后播放声音 在此设置监听器
        mGameSoundPool.getSoundPool().setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == 8)
                    mGameSoundPool.playSound(8, 0, mSound);
            }
        });
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.activity_start, this);
        mHighestScoreTextView = findViewById(R.id.highest_score_view);
        mSoundView = findViewById(R.id.sound_image_view);
        mStartButton = findViewById(R.id.btn_start_game);
        mResumeButton = findViewById(R.id.btn_resume_game);
        mAboutGameButton = findViewById(R.id.btn_about_game);
        mHighestScoreTextView.setTitle("最高分");
        mHighestScoreTextView.setScore(Repository.get(getContext(), Constants.HIGHEST_SCORE_KEY, 0));
    }

    public void setHighestScore(int value) {
        mHighestScoreTextView.setScore(value);
        // 更新最高分到SharedPreference
        Repository.put(getContext(), Constants.HIGHEST_SCORE_KEY, value);
    }

    private void initClick() {
        // 声音按钮按下的处理方法
        mSoundView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSound) {
                    mSound = false;
                    mSoundView.setImageDrawable(getResources().getDrawable(R.drawable.close_voice));
                } else {
                    mSound = true;
                    mSoundView.setImageDrawable(getResources().getDrawable(R.drawable.open_voice));
                }
                // 通知监听者
                Message message = Message.obtain();
                message.what = Constants.SOUND_CHANGE;
                message.arg1 = Constants.START_VIEW;
                mHandler.sendMessage(message);
            }
        });
        // 游戏开始按钮按下的处理方法
        mStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameSoundPool.playSound(4, 0, mSound);
                LogUtil.d(TAG, "start game button clicked");
                startFlash(mStartButton, 150, 3);
                // 从内存池返回新的Message 与mHandler进行绑定 比新建并申请内存更高效
                // Returns a new Message from the global message pool.
                // More efficient than creating and allocating new instances.
                final Message message = Message.obtain();
                message.what = Constants.START_GAME;
                mStartButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mStartButton.clearAnimation();
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });
        // 继续游戏按钮按下的处理方法
        mResumeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameSoundPool.playSound(4, 0, mSound);
                startFlash(mResumeButton, 150, 3);
                final Message message = Message.obtain();
                message.what = Constants.RESUME_GAME;
                mResumeButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mResumeButton.clearAnimation();
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
        // 关于游戏按钮按下的处理方法
        mAboutGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameSoundPool.playSound(4, 0, mSound);
                final Message message = Message.obtain();
                message.what = Constants.ABOUT_GAME;
                mHandler.sendMessageDelayed(message, 200);
            }
        });

    }

    // 设置进入游戏时按钮的闪烁
    private void startFlash(Button button, int duration, int repeatCount) {
        if (null == button)
            return;
        // 其中1.0表示完全不透明，0.0表示完全透明。
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(duration);
        // 设置此动画的加速度曲线。 默认为线性插值。
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(repeatCount);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        // 闪烁后恢复原来状态
        alphaAnimation.setFillBefore(true);
        button.startAnimation(alphaAnimation);
    }

    public boolean getSound() {
        return mSound;
    }

    public void setSound(boolean sound) {
        mSound = sound;
        if (mSound) {
            mSoundView.setImageDrawable(getResources().getDrawable(R.drawable.open_voice));
        } else {
            mSoundView.setImageDrawable(getResources().getDrawable(R.drawable.close_voice));
        }
    }

    public int getHighestScore() {
        return mHighestScoreTextView.getScore();
    }
}
