package com.example.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.popstar.R;
import com.example.utils.LogUtil;

/**
 * Created by Tinglan on 2020/10/15 23:13
 * It works!!
 */
public class DialogView extends Dialog {

    private static final String TAG = DialogView.class.getSimpleName();

    private View mLayout;
    private Button mExitGameButton;
    private Button mExitGameAndSaveButton;
    private TextView mTextView;
    private OnDialogViewListener mOnDialogViewListener = null;
    private TextView mTitleView;

    public DialogView(@NonNull Context context) {
        this(context, 0);
    }

    public DialogView(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mLayout = LayoutInflater.from(context).inflate(R.layout.dialog_normal_layout, null);
        setContentView(mLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        initView();
        initClick();
    }

    private void initClick() {
        // 调用监听者重写的按钮被按下的处理函数
        mExitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogViewListener != null) {
                    mOnDialogViewListener.onExitGameButtonClick();
                }
            }
        });
        mExitGameAndSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogViewListener != null) {
                    mOnDialogViewListener.onExitGameAndSaveButtonClick();
                }
            }
        });
    }

    private void initView() {
        mExitGameAndSaveButton = mLayout.findViewById(R.id.positiveButton);
        mExitGameButton = mLayout.findViewById(R.id.negativeButton);
        mTextView = mLayout.findViewById(R.id.message);
        mTitleView = mLayout.findViewById(R.id.title);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setPositiveButton(String text){
        mExitGameAndSaveButton.setText(text);
    }

    public void setNegativeButton(String text){
        mExitGameButton.setText(text);
    }

    public void setOnDialogViewListener(OnDialogViewListener onDialogViewListener) {
        mOnDialogViewListener = onDialogViewListener;
    }

    public interface OnDialogViewListener {
        void onExitGameButtonClick();

        void onExitGameAndSaveButtonClick();
    }

    @Override
    protected void onStop() {
        LogUtil.d(TAG,"onStop");
        mExitGameAndSaveButton.setOnClickListener(null);
        mExitGameButton.setOnClickListener(null);
        super.onStop();
    }
}
