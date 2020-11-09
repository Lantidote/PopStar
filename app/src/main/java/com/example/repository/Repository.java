package com.example.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.constants.Constants;
import com.example.view.StarBoard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Tinglan on 2020/10/14 13:48
 * It works!!
 */
public class Repository {

    public static void put(Context context, String key, Object value) {

        SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();

        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            // 按说应该抛异常。
        }

        editor.apply();
    }

    public static <P> P get(Context context, String key, P defValue) {
//    public static Object get(Context context, String key, Object defValue) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Object result = null;
        if (defValue instanceof Boolean) {
            result = prefs.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            result = prefs.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Integer) {
            result = prefs.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Long) {
            result = prefs.getLong(key, (Long) defValue);
        } else if (defValue instanceof String) {
            result = prefs.getString(key, (String) defValue);
        }
        return (P) result;
    }

    public static boolean putObject(Context context, Object starInfo, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(starInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String objectStr = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        if (objectOutputStream != null) {
            try {
                objectOutputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        editor.putString(key, objectStr);
        return editor.commit();
    }

    public static Object getObject(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String wordBase64 = sharedPreferences.getString(key, "");
        if (wordBase64 == null || wordBase64 == "") {
            return null;
        }
        try {
            byte[] objectBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((objectBytes));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
