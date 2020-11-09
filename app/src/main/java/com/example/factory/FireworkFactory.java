package com.example.factory;

import android.content.Context;

import com.example.animation.Firework;
import com.example.constants.Constants;
import com.example.popstar.R;

/**
 * Created by Tinglan on 2020/10/11 01:40
 * It works!!
 */
public class FireworkFactory {

    private final static int SMALL_DEFAULT_ELEMENT_COUNT = 8;
    private final static int BIG_DEFAULT_ELEMENT_COUNT = 200;
    private final static float SMALL_DEFAULT_LAUNCH_SPEED = 18.0f;
    private final static float BIG_DEFAULT_LAUNCH_SPEED = 3.0f;
    private static final int SMALL_DEFAULT_DURATION = 1300;
    private static final int BIG_DEFAULT_DURATION = 3000;
    private final static float DEFAULT_GRAVITY = 6.0f;
    
    public static Firework createBombFirework(Context context, int x, int y, int color){
        switch (color) {
            case Constants.RED:
                color = R.drawable.su_pink_block;
                break;
            case Constants.GREEN:
                color = R.drawable.su_lightgreen_block;
                break;
            case Constants.BLUE:
                color = R.drawable.su_blue_block;
                break;
            case Constants.YELLOW:
                color = R.drawable.su_yellow_block;
                break;
            case Constants.PURPLE:
                color = R.drawable.su_purple_block;
                break;
        }
        Firework firework = new Firework(context,x,y,color);
        firework.setGravity(DEFAULT_GRAVITY);
        firework.setLaunchSpeed(SMALL_DEFAULT_LAUNCH_SPEED);
        firework.setCount(SMALL_DEFAULT_ELEMENT_COUNT);
        firework.setDuration(SMALL_DEFAULT_DURATION);
        firework.initBombFirework();
        return firework;
    }

    // 生成开始界面使用的大烟花
    public static Firework createStartFirework(Context context, int x, int y){
        Firework firework = new Firework(context,x,y,0);
        firework.setGravity(DEFAULT_GRAVITY);
        firework.setCount(BIG_DEFAULT_ELEMENT_COUNT);
        firework.setLaunchSpeed(BIG_DEFAULT_LAUNCH_SPEED);
        firework.setDuration(BIG_DEFAULT_DURATION);
        firework.initStartFirework();
        return firework;
    }
}
