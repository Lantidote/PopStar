package com.example.popstar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import android.view.WindowManager;
import android.widget.Toast;

import com.example.sounds.GameSoundPool;
import com.example.utils.LogUtil;
import com.example.utils.PermissionsUtil;
import com.example.constants.Constants;
import com.example.view.DialogView;
import com.example.view.MainView;
import com.example.view.StartView;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private GameSoundPool mSounds = new GameSoundPool(this);
    private StartView mStartView;
    private MainView mMainView;
    private DialogView mDialogView = null;
    private Dialog mDialog = null;
    private boolean mDoubleBackToExitPressedOnce = false;
    private boolean mIsRelease = false; // 用于表示资源是否被释放
    private int mViewNumber = Constants.START_VIEW;

    // MainLooper由Android环境创建 不需要调用prepare函数
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.d(TAG, "handle message");
            switch (msg.what) {
                case Constants.START_GAME:
                    startGame();
                    break;
                case Constants.RESUME_GAME:
                    resumeGame();
                    break;
                case Constants.UPDATE_BOMB:
                    mMainView.bombStar();
                    break;
                case Constants.SOUND_CHANGE: {
                    if (msg.arg1 == Constants.START_VIEW) {
                        mMainView.setSound(mStartView.getSound());
                    } else if (msg.arg1 == Constants.MAIN_VIEW) {
                        mStartView.setSound(mMainView.getSound());
                    }
                    break;
                }
                case Constants.ABOUT_GAME: {
                    showAboutGameDialog();
                    break;
                }
            }
        }
    };

    private void resumeGame() {
        mDoubleBackToExitPressedOnce = false;
        mViewNumber = Constants.MAIN_VIEW;
        mMainView.resume(mStartView.getHighestScore(), mStartView.getSound());
        setContentView(mMainView);
    }

    private void startGame() {
        mDoubleBackToExitPressedOnce = false;
        mViewNumber = Constants.MAIN_VIEW;
        mMainView.startInit(mStartView.getHighestScore(), mStartView.getSound());
        setContentView(mMainView);
    }

    private void endGame() {
        // 更新最高分和声音
        mStartView.setHighestScore(mMainView.getHighestScore());
        mStartView.setSound(mMainView.getSound());
        mViewNumber = Constants.START_VIEW;
        setContentView(mStartView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");

        // 非发布模式 输出LOG
        LogUtil.init(this.getPackageName(), false);
        // 设置去除标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 初始化音乐
        mSounds.initGameSound();
        // 申请权限
        PermissionsUtil.requestPermission(this);

        mStartView = new StartView(this, mSounds, mHandler);
        mMainView = new MainView(this, mSounds, mHandler);
        // 给MainView设置布局监听 若stateBar不可见布局完毕后进行星星下落特效
        ViewTreeObserver viewTreeObserver = mMainView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LogUtil.d(TAG, "onGlobalLayout");
                if (!mMainView.getStateBarVisibility()) {
                    if (mMainView.getResumeFlag())
                        // 布局完毕了再设置imageView的可见性 不然可能会布局混乱 注意！！！
                        mMainView.resumeStar();
                    mMainView.fall();
                }
            }
        });
        // TODO:爆炸烟花
//        mHandler.postDelayed(mRunnable,1000);
        setContentView(mStartView);
        mMainView.setOnMainViewListener(new MainView.OnMainViewListener() {
            @Override
            public void onNextLevel() {
                setContentView(mMainView);
            }

            @Override
            public void onGameOver() {
                endGame();
            }
        });
    }

    public Handler getHandler() {
        return mHandler;
    }

    // 实现每2s燃放一次烟花
//    Runnable mRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if(mStartView != null && !mStartView.isPause()){
//                mStartView.bombFireworks();
//            }
//            mHandler.postDelayed(this,2000);
//        }
//    };


    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG,"onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(TAG,"onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
        // 结合实际情况也可以在onPause方法里 释放资源，但是这里释放资源有一个问题你需要知道并且避免：
        // 在onPause生命周期执行的瞬间，activity其实是还在前台的，所以有概率出现资源已经被释放，但是activity里的View是还有被点击的机会导致空指针报错（特别是在跑monkey的时候，容易出现这种报错）
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
        release();
    }

    private void release() {
        LogUtil.d(TAG,"release");
//        mSounds.releaseGameSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }

    // 菜单栏上的返回键被按下
    @Override
    public void onBackPressed() {
        LogUtil.d(TAG, "onBackPressed");
        // 获取Activity的content view
        if (mViewNumber == Constants.START_VIEW) {
            if (mDoubleBackToExitPressedOnce) {
                // TODO:释放资源 super.onBackPressed完成了finish()
                super.onBackPressed();
            }
            mDoubleBackToExitPressedOnce = true;
            Toast.makeText(this, this.getString(R.string.on_back_to_exit_expressed), Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDoubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else if (mViewNumber == Constants.MAIN_VIEW) {
            if (mDialogView != null && mDialogView.isShowing()) {
                // 退出此对话框，将其从屏幕上删除 在onStop上清理内容
                mDialogView.dismiss();
                mDialogView = null;
            } else {
                showAlertDialog();
            }
        }
    }

    // 在游戏界面中按下返回键弹出
    private void showAlertDialog() {
        mDialogView = new DialogView(this, R.style.Dialog);
        mDialogView.setText(this.getResources().getString(R.string.message_hint));
        mDialogView.setTitle(this.getResources().getString(R.string.hint));
        mDialogView.setPositiveButton(this.getResources().getString(R.string.exit_save_game));
        mDialogView.setNegativeButton(this.getResources().getString(R.string.exit_game));
        mDialogView.setCanceledOnTouchOutside(false);
        mDialogView.setOnDialogViewListener(new DialogView.OnDialogViewListener() {
            @Override
            public void onExitGameButtonClick() {
                mMainView.releaseAnimation();
                mDialogView.dismiss();
                endGame();
            }

            @Override
            public void onExitGameAndSaveButtonClick() {
                mMainView.save();
                mMainView.releaseAnimation();
                mDialogView.dismiss();
                endGame();
            }
        });
        mDialogView.show();
    }

    // 在开始界面按下"关于游戏"弹出
    private void showAboutGameDialog() {
        mDialog = new Dialog(this, R.style.Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.about_game, null);
        mDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.setCanceledOnTouchOutside(false);
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        mDialog.show();
    }
}

