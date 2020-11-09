package com.example.sounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.constants.Constants;
import com.example.popstar.R;
import com.example.utils.Utils;

import java.util.HashMap;

/**
 * Created by Tinglan on 2020/9/13 21:31
 * It works!!
 */
public class GameSoundPool {
    private Context mContext;
    private HashMap<Integer, Integer> soundMap = new HashMap<>();
    private SoundPool soundPool;

    public GameSoundPool(Context context) {
        mContext = context;
        // 先set所有属性再build
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(8); //传入音频数量
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC); //设置音频流的合适的属性
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
    }

    public void initGameSound() {
        //
        soundMap.put(8, soundPool.load(mContext, R.raw.welcome, 1));//首次进入
        soundMap.put(4, soundPool.load(mContext, R.raw.button_start, 1));//点击按钮
        soundMap.put(1, soundPool.load(mContext, R.raw.select, 1));//选中砖块
        soundMap.put(2, soundPool.load(mContext, R.raw.applause, 1));//游戏鼓励
        soundMap.put(3, soundPool.load(mContext, R.raw.su_gameover, 1));//游戏结束
        //map.put(5, soundPool.load(mContext,R.raw.landing, 1));//游戏开始放砖块
        soundMap.put(6, soundPool.load(mContext, R.raw.broken, 1));//砖块破裂
        soundMap.put(7, soundPool.load(mContext, R.raw.round_clear, 1));//游戏胜利
        soundMap.put(9, soundPool.load(mContext, R.raw.fireworks_03, 1));//主菜单烟火爆炸
        soundMap.put(10, soundPool.load(mContext, R.raw.discovery_02, 1));//达到目标分
        soundMap.put(11, soundPool.load(mContext, R.raw.get_star_prop, 1));//金币
        soundMap.put(12, soundPool.load(mContext, R.raw.fireworks_02, 1));//顺利烟花
        soundMap.put(13, soundPool.load(mContext, R.raw.landing, 1));//砖块落地
    }

    public void releaseGameSound(){
        soundPool.release();
    }

    public void playSound(int sound, int loop, boolean isPlay) {
        if (isPlay) {
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            float streamVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            float streamMaxVolumeCurrent = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//            float volume = streamVolumeCurrent / streamMaxVolumeCurrent;
            float volume = streamMaxVolumeCurrent / streamMaxVolumeCurrent;
            soundPool.play(soundMap.get(sound), volume, volume, 1, loop, 1.0f);
        }
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }
}
