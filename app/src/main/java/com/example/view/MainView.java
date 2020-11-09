package com.example.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.constants.Constants;
import com.example.popstar.R;
import com.example.repository.Repository;
import com.example.sounds.GameSoundPool;
import com.example.utils.LogUtil;


/**
 * Created by Tinglan on 2020/9/16 21:28
 * It works!!
 */
public class MainView extends RelativeLayout implements StarBoard.OnStarBoardListener, AnimationView.OnAnimationViewListener {

    private static final String TAG = "MainView";

    private final GameSoundPool mGameSoundPool;
    private boolean mSound;
    private Handler mHandler;
    private ScoreTextView mHighestScoreTextView;
    private ScoreTextView mLevelView;
    private ScoreTextView mTargetView;
    private ScoreTextView mCurrentView;
    private ImageView mSoundView;
    private int mTargetScore = 1000;
    private int mCurrentScore = 0;
    private int mLevel = 1;
    private int mHighestScore = 0;
    private RelativeLayout mStateBar;
    private StarBoard mStarBoard;
    private OnMainViewListener mOnMainViewListener = null;
    private TextView mBreakStarView;
    private boolean resumeFlag;

    public MainView(Context context, GameSoundPool gameSoundPool, Handler handler) {
        this(context, null, gameSoundPool, handler);
    }

    public MainView(Context context, AttributeSet attrs, GameSoundPool gameSoundPool, Handler handler) {
        this(context, attrs, 0, gameSoundPool, handler);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr,
                    GameSoundPool gameSoundPool, Handler handler) {
        super(context, attrs, defStyleAttr);
        mHandler = handler;
        mGameSoundPool = gameSoundPool;
        mStarBoard = new StarBoard(context);
        mStarBoard.setOnStarBoardListener(this);
        initView(context);
        addView(mStarBoard);
        initClick();
    }

    private void initClick() {
        mSoundView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSound) {
                    mSound = false;
                } else {
                    mSound = true;
                }
                changeSoundImage();
                // 通知监听者
                Message message = Message.obtain();
                message.what = Constants.SOUND_CHANGE;
                message.arg1 = Constants.MAIN_VIEW;
                mHandler.sendMessage(message);
            }
        });
    }

    private void initView(Context context) {
        // RelativeLayout下有activity_game
        LayoutInflater.from(context).inflate(R.layout.activity_game, this);
        mHighestScoreTextView = findViewById(R.id.highest_score_view_game);
        mLevelView = findViewById(R.id.level_view);
        mTargetView = findViewById(R.id.target_view);
        mCurrentView = findViewById(R.id.current_score_view);
        mSoundView = findViewById(R.id.pause_image_view);
        mStateBar = findViewById(R.id.state_bar);
        mBreakStarView = findViewById(R.id.break_star_score_text_view);
        // 设置名称及对应值
        mHighestScoreTextView.setTitle("最高分");
        mLevelView.setTitle("关卡");
        mLevelView.setScoreLayoutWidth(50);
        mTargetView.setTitle("目标分");
        mTargetView.setScoreLayoutWidth(100);
        // 设置动画的监听
        AnimationView.setOnAnimationViewListener(this);
    }

    // 实现星星下落特效后设置状态栏为可见
    public void fall() {
        resumeFlag = false;
        mStarBoard.fall();
    }

    // 布局完成后调用
    public void resumeStar() {
        if (!mStarBoard.resume()) {
            startInit(mHighestScore, mSound);
        }
    }

    public boolean getStateBarVisibility() {
        return mStateBar.getVisibility() == VISIBLE;
    }

    public void startInit(int highestScore, boolean sound) {
        mHighestScore = highestScore;
        mSound = sound;
        changeSoundImage();
        mLevel = 1;
        mTargetScore = 1000;
        mCurrentScore = 0;
        mHighestScoreTextView.setScore(mHighestScore);
        mLevelView.setScore(mLevel);
        mTargetView.setScore(mTargetScore);
        mCurrentView.setScore(mCurrentScore);
        mBreakStarView.setText("0 块 0 分");
        mStarBoard.init();
        // 先设置为不可见 待星星落完后显示
        mStateBar.setVisibility(INVISIBLE);
        mBreakStarView.setVisibility(INVISIBLE);
        resumeFlag = false;
    }

    public void resume(int highestScore, boolean sound) {
        mHighestScore = highestScore;
        mSound = sound;
        changeSoundImage();
        mLevel = Repository.get(getContext(), Constants.SAVE_LEVEL, 1);
        mTargetScore = Repository.get(getContext(), Constants.SAVE_TARGET_SCORE, 1000);
        mCurrentScore = Repository.get(getContext(), Constants.SAVE_CURRENT_SCORE, 0);
        mHighestScoreTextView.setScore(mHighestScore);
        mLevelView.setScore(mLevel);
        mTargetView.setScore(mTargetScore);
        mCurrentView.setScore(mCurrentScore);
        mBreakStarView.setText("0 块 0 分");
        mStarBoard.init();
        mStateBar.setVisibility(INVISIBLE);
        mBreakStarView.setVisibility(INVISIBLE);
        resumeFlag = true;
    }

    public boolean getSound() {
        return mSound;
    }

    public void setSound(boolean sound) {
        mSound = sound;
        changeSoundImage();
    }

    public boolean getResumeFlag() {
        return resumeFlag;
    }

    // 根据mSound的值改变声音图标
    private void changeSoundImage() {
        if (mSound)
            mSoundView.setImageDrawable(getResources().getDrawable(R.drawable.open_voice));
        else
            mSoundView.setImageDrawable(getResources().getDrawable(R.drawable.close_voice));
    }

    public int getHighestScore() {
        return mHighestScoreTextView.getScore();
    }

    public void save() {
        // 如果没有可消除的星星且得分低于目标分则返回
        if (mStarBoard.checkNoStarBlock() && mCurrentScore < mTargetScore)
            return;
        LogUtil.d(TAG, "save game");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 保存当前分数、当前关卡、目标分
                Repository.put(getContext(), Constants.SAVE_LEVEL, mLevel);
                Repository.put(getContext(), Constants.SAVE_CURRENT_SCORE, mCurrentScore);
                Repository.put(getContext(), Constants.SAVE_TARGET_SCORE, mTargetScore);
                // toJson返回String
//                Repository.put(getContext(), Constants.SAVED_STAR_BOARD, new Gson().toJson(mStarBoard));
                mStarBoard.save();
                LogUtil.d(TAG, "save success");
            }
        });
        thread.start();
    }

    // 开场星星落完后执行
    @Override
    public void onStarAllFall() {
        mStateBar.setVisibility(VISIBLE);
        mBreakStarView.setVisibility(VISIBLE);
        // 设置目标分闪烁
        startFlash(mTargetView, 200, 3);
        // 达到目标分显示通关动画
        if (mCurrentScore > mTargetScore) {
            mStarBoard.addAnimation(Constants.CONGRATULATIONS_ON_PASS);
        }
    }

    // 设置目标分闪烁
    private void startFlash(ScoreTextView scoreTextView, int duration, int repeatCount) {
        if (null == scoreTextView)
            return;
        // 其中1.0表示完全不透明，0.0表示完全透明。
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(duration);
        // 设置此动画的加速度曲线。 默认为线性插值。
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(repeatCount);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        // 闪烁后恢复原来状态
        LinearLayout linearLayout = (LinearLayout) (scoreTextView.getChildAt(0));
        linearLayout.getChildAt(1).startAnimation(alphaAnimation);
    }

    public void bombStar() {
        // 爆破剩余星星
        if (mStarBoard.getBombStarNumber() > 0) {
            mGameSoundPool.playSound(6, 0, mSound);
            mStarBoard.bombStar();
            Message message = Message.obtain();
            message.what = Constants.UPDATE_BOMB;
            mHandler.sendMessageDelayed(message, 250);
        } else {
            onAllStarBomb();
        }
    }

    // 闯关完成并播放完爆炸动画后
    public void onAllStarBomb() {
        releaseAnimation();
        // 判断是否过关
        if (mCurrentScore >= mTargetScore) {
            mStarBoard.init();
            // 更新状态栏信息
            updateStateBar();
            if (mOnMainViewListener != null) {
                mOnMainViewListener.onNextLevel();
            }
        } else {
            // TODO:显示复活界面 更新最高分
            if (mOnMainViewListener != null) {
                mOnMainViewListener.onGameOver();
            }
        }
    }

    public void releaseAnimation() {
        // 清除动画
        mStarBoard.releaseAnimationView();
    }

    @Override
    public void onScoreChange(int value, int situation) {
        switch (situation) {
            case Constants.STAR_BROCK: {
                // 添加分数动画 播放音效
                if(value >= Constants.SCORE_COOL && value < Constants.SCORE_AWESOME){
                    mStarBoard.addAnimation(Constants.COOL);
                    mGameSoundPool.playSound(2,0,mSound);
                }else if(value >= Constants.SCORE_AWESOME && value < Constants.SCORE_FANTASTIC){
                    mStarBoard.addAnimation(Constants.AWESOME);
                    mGameSoundPool.playSound(2,0,mSound);
                }else if(value >= Constants.SCORE_FANTASTIC){
                    mStarBoard.addAnimation(Constants.FANTASTIC);
                    mGameSoundPool.playSound(2,0,mSound);
                }
                LogUtil.d(TAG, "砖块破裂");
                int changedScore = value * value * 5;
                mCurrentScore += changedScore;
                mBreakStarView.setText(value + " 块 " + changedScore + " 分");
                mGameSoundPool.playSound(6, 0, mSound);
                break;
            }
            case Constants.BONUS_SCORE: {
                mStarBoard.addAnimation(Constants.DISPLAY_PASS_BONUS, String.valueOf(value));
                if (value < 10)
                    mCurrentScore += 2000 - value * value * 20;
                // 播放音效
                if (mCurrentScore >= mTargetScore) {
                    mGameSoundPool.playSound(2, 0, mSound);
                } else {
                    mGameSoundPool.playSound(3, 0, mSound);
                }
                break;
            }
        }
        mCurrentView.setScore(mCurrentScore);
        // 更新最高分
        if (mCurrentScore > mHighestScore) {
            mHighestScore = mCurrentScore;
            mHighestScoreTextView.setScore(mHighestScore);
            // 更新SharedPreference的最高分
            Repository.put(getContext(), Constants.HIGHEST_SCORE_KEY, mHighestScore);
        }
        // 当前分大于目标分弹出通关动画 不能在此播放声音 因为只有在初次通关后需要播放
        if (mCurrentScore >= mTargetScore) {
            mStarBoard.addAnimation(Constants.CONGRATULATIONS_ON_PASS);
        }
    }

    @Override
    public void onPass() {
        mGameSoundPool.playSound(10, 0, mSound);
    }

    public void setOnMainViewListener(OnMainViewListener onMainViewListener) {
        mOnMainViewListener = onMainViewListener;
    }

    public interface OnMainViewListener {
        void onNextLevel();

        void onGameOver();
    }

    private void updateStateBar() {
        mLevel++;
        mLevelView.setScore(mLevel);
        if (mLevel % 3 == 0) {
            mTargetScore += 3000;
        } else if (mLevel > 10) {
            mTargetScore += 4000;
        } else {
            mTargetScore += 2000;
        }
        mTargetView.setScore(mTargetScore);
        mBreakStarView.setText("0 块 0 分");
        mStateBar.setVisibility(INVISIBLE);
        mBreakStarView.setVisibility(INVISIBLE);
    }

}
