package com.example.view;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


import com.example.animation.Firework;
import com.example.constants.Constants;
import com.example.factory.FireworkFactory;
import com.example.popstar.MainActivity;
import com.example.popstar.R;
import com.example.repository.Repository;
import com.example.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Tinglan on 2020/9/23 16:50
 * It works!!
 */
// 用于记录整个星星棋盘
public class StarBoard extends ViewGroup implements View.OnClickListener {

    private static final String TAG = "StarBoard";
    private static final int DEFAULT_DURATION = 50;

    private List<List<ImageView>> mStarList = new ArrayList<>();
    private List<ImageView> mBombStarList = new ArrayList<>();
    private StarInfo[][] mStarsInfo = new StarInfo[Constants.ROW][Constants.COLUMN];
    private List<StarInfo> mSelectedStarsInfo = new ArrayList<>();
    // 用于点击后的爆炸效果
    // 在DFS中记录底层的星星 用于下落时用
    private HashMap<Integer, Integer> mBottomStarMap = new HashMap<>();
    private int[] mTopBoundaryList = new int[Constants.COLUMN];
    private int mRightBoundary = Constants.COLUMN - 1;
    private int mSelectedStarNum;
    private OnStarBoardListener mOnStarBoardListener = null;
    // 用于动画效果
    private AnimationView mAnimationView;

    public StarBoard(Context context) {
        this(context, null);
    }

    public StarBoard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        // 默认ViewGroup不调用onDraw方法 因此需要先设置该Flag
//        setWillNotDraw(false);
        for (int i = 0; i < Constants.ROW; i++) {
            List<ImageView> line = new ArrayList<>();
            mStarList.add(line);
            for (int j = 0; j < Constants.COLUMN; j++) {
                // 此处特别注意！！！若父容器设置为null 则view.getLayoutParams() == null
                // 若父容器一定要设置为null 需要自行新建LayoutParams并在添加子view时使用
                // addView(view, params)才能正确体现View布局的宽高参数
//                ImageView imageView = (ImageView) (LayoutInflater.from(getContext()).inflate(R.layout.star_image_view,null));
                ImageView imageView = (ImageView) (LayoutInflater.from(getContext()).inflate(R.layout.star_image_view, this, false));
//                LogUtil.d(TAG,"imageView.getLayoutParams() == null? " + (imageView.getLayoutParams() == null));
                initStarImageView(imageView);
                // 设置点击监听
                imageView.setOnClickListener(this);
                StarInfo starInfo = new StarInfo(Constants.RED, false, false);
                // mStarInfos按xy的顺序存储 mStarList按行列的顺序存储 注意！ x对应列数 y对应行数
                mStarsInfo[j][i] = starInfo;
                line.add(imageView);
                addView(imageView);
            }
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // 添加特效View
        mAnimationView = new AnimationView(getContext());
        mAnimationView.setLayoutParams(layoutParams);
        addView(mAnimationView);
    }

    // 设置星星的宽度和高度和屏幕适配
    private void initStarImageView(ImageView imageView) {
        LayoutParams layoutParams = imageView.getLayoutParams();
//        LogUtil.d(TAG,"width ==> " + layoutParams.width + " height ==> " + layoutParams.height);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        layoutParams.width = metrics.widthPixels / Constants.ROW;
        layoutParams.height = layoutParams.width;
        // !!!!!!!
        imageView.setLayoutParams(layoutParams);
        // 关闭按钮默认的点击音效
        imageView.setSoundEffectsEnabled(false);
        //  在onCreate方法中，控件其实还没有画好，也就是说当onCreate方法执行完了之后，才开始绘制控件，所以在onCreate方法中获取width和height返回的值是0
//        LogUtil.d(TAG,"imageView.getWidth() ==> " + imageView.getWidth() + " imageView.getHeight() ==> " + imageView.getHeight());
    }

    // 使用此函数重新初始化 不需要再创建ImageView等
    public void init() {
        Random random = new Random(System.currentTimeMillis());
        mRightBoundary = Constants.COLUMN - 1;
        for (int i = 0; i < Constants.COLUMN; i++) {
            for (int j = 0; j < Constants.ROW; j++) {
                int color = random.nextInt(5);
                ImageView imageView = mStarList.get(j).get(i);
                setImageViewColor(imageView, color);
                imageView.setVisibility(VISIBLE);
                mStarsInfo[i][j].visited = mStarsInfo[i][j].removed = false;
                mStarsInfo[i][j].color = color;
            }
            mTopBoundaryList[i] = Constants.ROW - 1;
        }
        setStarClickable(false);
    }

    public void save() {
        // 储存mStarsInfo
        for (int i = 0; i < Constants.ROW; i++) {
            for (int j = 0; j < Constants.COLUMN; j++) {
                Boolean saveSuccess = Repository.putObject(getContext(), new StarInfo(mStarsInfo[i][j]), Constants.SAVE_STAR + i + j);
                if (!saveSuccess) {
                    LogUtil.d(TAG, "save star " + i + " " + j + " fail");
                }
            }
        }
        // 储存mRightBoundary和mTopBoundaryList
        Repository.put(getContext(), Constants.SAVE_RIGHT_BOUNDARY, mRightBoundary);
        for (int i = 0; i < mTopBoundaryList.length; i++) {
            Repository.put(getContext(), Constants.SAVE_TOP_BOUNDARY + i, mTopBoundaryList[i]);
        }
    }


    private void setImageViewColor(ImageView imageView, int color) {
        switch (color) {
            case Constants.RED: {
                imageView.setImageDrawable(getContext().getDrawable(R.drawable.block_red));
                break;
            }
            case Constants.BLUE: {
                imageView.setImageDrawable(getContext().getDrawable(R.drawable.block_blue));
                break;
            }
            case Constants.GREEN: {
                imageView.setImageDrawable(getContext().getDrawable(R.drawable.block_green));
                break;
            }
            case Constants.YELLOW: {
                imageView.setImageDrawable(getContext().getDrawable(R.drawable.block_yellow));
                break;
            }
            case Constants.PURPLE: {
                imageView.setImageDrawable(getContext().getDrawable(R.drawable.block_purple));
                break;
            }
        }
    }

    // onMeasure->onLayout(布局)->onDraw(绘制界面)
    // 由layout调用 在重写的onLayout方法中，它们应该为每一子view调用layout方法进行布局。递归思想
    // 在layout中，先调用setFrame执行当前布局，再调用onLayout以对当前布局的子布局进行递归布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtil.d(TAG, "onLayout");
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mAnimationView.layout(0, 0, metrics.widthPixels, metrics.heightPixels);
        int currentLeft = 0;
        int currentRight = 0;
        int currentTop = metrics.heightPixels;
        int currentBottom = metrics.heightPixels;
        for (List<ImageView> line : mStarList) {
            for (ImageView star : line) {
                currentRight = currentLeft + star.getMeasuredWidth();
                currentTop = currentBottom - star.getMeasuredHeight();
                star.layout(currentLeft, currentTop, currentRight, currentBottom);
                currentLeft += star.getMeasuredWidth();
            }
            currentLeft = currentRight = 0;
            currentBottom = currentTop;
        }
    }

    // 一个view的实际测量工作是在被measure方法所调用的onMeasure(int，int)方法中实现的
    // 测量的基类实现默认为背景的尺寸 此处需要使用整个屏幕进行星星的下落等特效绘制 因此不重写onMeasure方法

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtil.d(TAG, "onMeasure");
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.AT_MOST);
//        LogUtil.d(TAG,"parentWidth ==> " + parentWidth + " parentHeight ==> "
//                + parentHeight);
        // 测量孩子
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
        }
        // 测量自己
        setMeasuredDimension(parentWidth, parentHeight);
    }

    // 转场时的下落动画
    public void fall() {
        // 获取最靠右且最高的坐标
        int top = mTopBoundaryList[0], x = 0;
        for (int i = 1; i <= mRightBoundary; i++) {
            if (mTopBoundaryList[i] >= top) {
                top = mTopBoundaryList[i];
                x = i;
            }
        }
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int count = 0;
        int duration = 700;
        for (int i = 0; i <= top; i++) {
            List<ImageView> line = mStarList.get(i);
            for (int j = 0; j <= mRightBoundary; j++) {
                if (!mStarsInfo[j][i].removed) {
                    ImageView star = line.get(j);
                    TranslateAnimation translateAnimation;
                    if (count % 2 == 0) {
                        translateAnimation = new TranslateAnimation(0, 0, -metrics.heightPixels, 0);
                        translateAnimation.setDuration(duration);
                    } else {
                        translateAnimation = new TranslateAnimation(0, 0, -metrics.heightPixels - star.getHeight(), 0);
                        translateAnimation.setDuration(duration + 50);
                    }
                    translateAnimation.setInterpolator(new AccelerateInterpolator());
                    star.startAnimation(translateAnimation);
                    count++;
                    if (i == top && j == x) {
                        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (mOnStarBoardListener != null) {
                                    mOnStarBoardListener.onStarAllFall();
                                }
                                setStarClickable(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
            }
            duration += DEFAULT_DURATION;
        }
    }

    public void setOnStarBoardListener(OnStarBoardListener listener) {
        // 此处设置MainView作为监听者 需要改变StateBar或播放音效时通知监听者
        mOnStarBoardListener = listener;
    }

    public void addAnimation(int type, String... value) {
        mAnimationView.addAnimation(type, value);
    }

    public boolean resume() {
        for (int i = 0; i < Constants.ROW; i++) {
            for (int j = 0; j < Constants.COLUMN; j++) {
                StarInfo starInfo = (StarInfo) (Repository.getObject(getContext(), Constants.SAVE_STAR + i + j));
                if (starInfo != null) {
                    mStarsInfo[i][j] = starInfo;
                    if (mStarsInfo[i][j].removed) {
//                        mStarList.get(j).get(i).setVisibility(INVISIBLE);
                        mStarList.get(j).get(i).setVisibility(GONE);
                    } else {
                        mStarList.get(j).get(i).setVisibility(VISIBLE);
                        setImageViewColor(mStarList.get(j).get(i), mStarsInfo[i][j].color);
                    }
                } else {
                    return false;
                }
            }
        }
        mRightBoundary = Repository.get(getContext(), Constants.SAVE_RIGHT_BOUNDARY, Constants.COLUMN - 1);
        for (int i = 0; i < mTopBoundaryList.length; i++) {
            mTopBoundaryList[i] = Repository.get(getContext(), Constants.SAVE_TOP_BOUNDARY + i, Constants.COLUMN - 1);
        }
        return true;
    }

    interface OnStarBoardListener {
        // 在转场时使用 所有星星下落完毕显示上端的状态栏
        void onStarAllFall();

        void onScoreChange(int value, int situation);
    }

    // 处理星星点击事件
    @Override
    public void onClick(View v) {
        LogUtil.d(TAG, "Star clicked");
        findSelectedStars((ImageView) v);
        LogUtil.d(TAG, "clicked star number ==> " + mSelectedStarNum);
        if (mSelectedStarNum < 2) {
            mStarsInfo[getImageViewX((ImageView) v)][getImageViewY((ImageView) v)].visited = false;
            return;
        }
        // 增加分数
        if (mOnStarBoardListener != null) {
            mOnStarBoardListener.onScoreChange(mSelectedStarNum, Constants.STAR_BROCK);
        }
        starsFall();
        // 判断星星是否需要左移并移动
        starMoveLeft();
        // 检查是否还有可消除的星星 没有的话判断是否进入下一关
        starCheck();
    }

    private void starCheck() {
        if (checkNoStarBlock() && mOnStarBoardListener != null) {
            // 将需要爆炸的星星添加进List
            mBombStarList.clear();
            for (int row = Constants.ROW - 1; row >= 0; row--) {
                for (int column = mRightBoundary; column >= 0; column--) {
                    if (!mStarsInfo[column][row].removed) {
                        mBombStarList.add(mStarList.get(row).get(column));
                    }
                }
            }
            LogUtil.d(TAG, "remain star ==> " + mBombStarList.size());
            // 计算奖励分 添加奖励分动画
            mOnStarBoardListener.onScoreChange(mBombStarList.size(), Constants.BONUS_SCORE);
            Message message = Message.obtain();
            message.what = Constants.UPDATE_BOMB;
            ((MainActivity) getContext()).getHandler().sendMessageDelayed(message, 250);
        }
    }

    /**
     * @return 返回是否还有可消除的星星块
     */
    public boolean checkNoStarBlock() {
        for (int x = 0; x <= mRightBoundary; x++) {
            for (int y = 0; y <= mTopBoundaryList[x]; y++) {
                findSelectedStars(mStarList.get(y).get(x));
                if (mSelectedStarNum > 1) {
                    for (StarInfo starInfo : mSelectedStarsInfo)
                        starInfo.visited = false;
                    return false;
                }
                mStarsInfo[x][y].visited = false;
            }
        }
        return true;
    }

    public void releaseAnimationView() {
        mAnimationView.release();
    }

    // 释放资源
    public void release() {
        // TODO:释放ImageView的资源 需要手动调用recycle并且手动调用setImageDrawable(null)并移除view
    }

    public void bombStar() {
        ImageView imageView = mBombStarList.get(0);
        imageView.setVisibility(INVISIBLE);
        mBombStarList.remove(imageView);
        int x = getImageViewX(imageView);
        int y = getImageViewY(imageView);
        launchFirework(imageView.getLeft(), imageView.getTop(), mStarsInfo[x][y].color);
    }

    public int getBombStarNumber() {
        return mBombStarList.size();
    }

    private void findSelectedStars(ImageView star) {
        mBottomStarMap.clear();
        mSelectedStarsInfo.clear();
        mSelectedStarNum = 0;
        int x = getImageViewX(star);
        int y = getImageViewY(star);
//        LogUtil.d(TAG,"clicked star x ==> " + x +" clicked star y ==> " + y);
        DFS(x, y);
    }

    private void DFS(int x, int y) {
//        LogUtil.d(TAG,"x ==> " + x + " y ==> " +y);
        mSelectedStarNum++;
        mSelectedStarsInfo.add(mStarsInfo[x][y]);
        mStarsInfo[x][y].visited = true;
        // 右 左 上 下
        int[][] direction = {{0, -1}, {1, 0}, {-1, 0}, {0, 1}};
        for (int i = 0; i < direction.length; i++) {
            int newX = x + direction[i][0], newY = y + direction[i][1];
            if (newX >= 0 && newX <= Constants.ROW - 1 && newY >= 0 && newY <= Constants.COLUMN - 1 &&
                    !mStarsInfo[newX][newY].visited && !mStarsInfo[newX][newY].removed &&
                    mStarsInfo[newX][newY].color == mStarsInfo[x][y].color) {
                DFS(newX, newY);
            }
        }
        boolean flag = true;
        // 底下还有与其同色的星星块
        for (int i = y - 1; i >= 0; i--) {
            if (mStarsInfo[x][i].visited) {
                flag = false;
                break;
            }
        }
        if (flag) {
            mBottomStarMap.put(x, y);
        }
    }

    // 用于点击后的星星移动
    private void starsFall() {
        LogUtil.d(TAG, "starFall");
        for (int x : mBottomStarMap.keySet()) {
            // 对于有星星被选中的列 从下往上遍历
            LogUtil.d(TAG, "x ==> " + x + " bottom ==> " + mBottomStarMap.get(x));
            int delete = 0;
            for (int y = mBottomStarMap.get(x); y <= mTopBoundaryList[x]; y++) {
                if (mStarsInfo[x][y].visited) {
                    mStarsInfo[x][y].removed = true;
                    ImageView star = mStarList.get(y).get(x);
                    // 在被删除的星星处发射烟花
                    launchFirework(star.getLeft(), star.getTop(), mStarsInfo[x][y].color);
                    delete++;
                } else {
                    // 交换StarInfo
                    StarInfo starInfo = mStarsInfo[x][y - delete];
                    mStarsInfo[x][y - delete] = mStarsInfo[x][y];
                    mStarsInfo[x][y] = starInfo;
                    ImageView imageView = mStarList.get(y - delete).get(x);
                    setImageViewColor(imageView, mStarsInfo[x][y - delete].color);
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -delete, Animation.RELATIVE_TO_SELF, 0);
                    translateAnimation.setDuration(DEFAULT_DURATION * delete);
                    translateAnimation.setInterpolator(new AccelerateInterpolator());
                    imageView.startAnimation(translateAnimation);
                }
            }
            for (int y = mTopBoundaryList[x]; y > mTopBoundaryList[x] - delete; y--) {
                mStarList.get(y).get(x).setVisibility(INVISIBLE);
            }
            mTopBoundaryList[x] -= delete;
            LogUtil.d(TAG, "top boundary[" + x + "] ==> " + mTopBoundaryList[x]);
        }
    }

    private void launchFirework(int x, int y, int color) {
        LogUtil.d(TAG, "launchFirework x ==> " + x + " y ==> " + y);
        Firework firework = FireworkFactory.createBombFirework(getContext(), x, y, color);
        mAnimationView.addFirework(firework);
    }


    private void starMoveLeft() {
        int delete = 0;
        for (int x = 0; x <= mRightBoundary; x++) {
            if (mTopBoundaryList[x] == -1) {
                delete++;
                LogUtil.d(TAG, "delete ==> " + delete + " x ==> " + x);
            } else if (delete > 0) {
                // 对整列进行处理
                for (int y = 0; y <= mTopBoundaryList[x]; y++) {
                    // 交换StarInfo
                    StarInfo starInfo = mStarsInfo[x][y];
                    mStarsInfo[x][y] = mStarsInfo[x - delete][y];
                    mStarsInfo[x - delete][y] = starInfo;
                    ImageView imageView = mStarList.get(y).get(x - delete);
                    imageView.setVisibility(VISIBLE);
                    mStarList.get(y).get(x).setVisibility(INVISIBLE);
                    setImageViewColor(imageView, mStarsInfo[x - delete][y].color);
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, delete,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    translateAnimation.setDuration(DEFAULT_DURATION * delete);
                    translateAnimation.setInterpolator(new AccelerateInterpolator());
                    imageView.startAnimation(translateAnimation);
                }
                mTopBoundaryList[x - delete] = mTopBoundaryList[x];
            }
        }
        for (int x = mRightBoundary; x > mRightBoundary - delete; x--) {
            for (int y = 0; y <= mTopBoundaryList[x]; y++) {
                mStarList.get(y).get(x).setVisibility(INVISIBLE);
            }
            mTopBoundaryList[x] = -1;
        }
        mRightBoundary -= delete;
    }

    int getImageViewX(ImageView imageView) {
        return imageView.getLeft() / imageView.getWidth();
    }

    int getImageViewY(ImageView imageView) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return (metrics.heightPixels - imageView.getBottom()) / imageView.getHeight();
    }

    public void setStarClickable(boolean isAllow) {
        for (List<ImageView> line : mStarList) {
            for (ImageView star : line) {
                star.setClickable(isAllow);
            }
        }
    }
}
