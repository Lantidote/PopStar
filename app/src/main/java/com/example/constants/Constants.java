package com.example.constants;

/**
 * Created by Tinglan on 2020/9/13 23:30
 * It works!!
 */
public class Constants {

    // 颜色
    public final static int RED = 0;//红色
    public final static int GREEN = 1;//绿色
    public final static int BLUE = 2;//蓝色
    public final static int YELLOW = 3;//黄色
    public final static int PURPLE = 4;//紫色

    // 星星的行数和列数
    public final static int ROW = 10;
    public final static int COLUMN = 10;

    // view判断
    public final static int START_VIEW = 0;
    public final static int MAIN_VIEW = 1;

    // 加分时判断是奖励分还是消灭星星得分
    public final static int STAR_BROCK = 0;
    public final static int BONUS_SCORE = 1;

    // 动画类型判断
    public final static int CONGRATULATIONS_ON_PASS = 0;
    public final static int DISPLAY_PASS_BONUS = 1;
    // 用于选择GoodScore的Bitmap类型
    public final static int COOL = 2;
    public final static int AWESOME = 3;
    public final static int FANTASTIC = 4;

    // 不同动画的分界点
    public static final int SCORE_COOL = 6;
    public static final int SCORE_AWESOME = 9;
    public static final int SCORE_FANTASTIC = 14;

    // 用于SharedPreference的KEY值
    public final static String HIGHEST_SCORE_KEY = "bestScore";
    public final static String SAVE_LEVEL = "saveLevel";
    public final static String SAVE_CURRENT_SCORE = "saveCurrentScore";
    public final static String SAVE_TARGET_SCORE = "saveTargetScore";
    public final static String SAVE_STAR = "saveStar";
    public final static String SAVE_RIGHT_BOUNDARY = "saveRightBoundary";
    public final static String SAVE_TOP_BOUNDARY = "saveTopBoundary";

    public final static int START_GAME = 1;
    public final static int RESUME_GAME = 2;
    public final static int UPDATE_BOMB = 3;
    public final static int SOUND_CHANGE = 4;
    public final static int ABOUT_GAME = 5;


}
