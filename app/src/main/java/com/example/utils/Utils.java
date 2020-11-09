package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Tinglan on 2020/9/13 23:00
 * It works!!
 */
public class Utils {

    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final Canvas sCanvas = new Canvas();

    private static final String dbNmae = "clearStar";

    public static int getKeyDefault(Context ct, String key) {
        int n = 0;
        if (ct != null) {
            SharedPreferences mSharedPreferences = ct.getSharedPreferences(
                    dbNmae, Context.MODE_PRIVATE);
            // defValue key不存在时返回的值
            n = mSharedPreferences.getInt(key, 1);
        }
        return n;
    }

    public static int getKey(Context ct, String key) {
        int n = 0;
        if (ct != null) {
            SharedPreferences mSharedPreferences = ct.getSharedPreferences(
                    dbNmae, Context.MODE_PRIVATE);
            // defValue key不存在时返回的值
            n = mSharedPreferences.getInt(key, 0);
        }
        return n;
    }

    /**
     * 画出指定形状的图片
     *
     * @param bmp
     * @param radius 半径
     * @param shape_type
     * @return
     */
    public static Bitmap drawShapeBitmap(Bitmap bmp, int radius,
                                         String shape_type) {
        Bitmap squareBitmap;// 根据传入的位图截取合适的正方形位图
        Bitmap scaledBitmap;// 根据直径对截取的正方形位图进行缩放

        int diameter = radius * 2;
        // 传入位图的宽高
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        if (h > w) {// 如果高>宽
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, 0, (h - w) / 2, w,
                    w);
        } else if (h < w) {// 如果宽>高
            squareBitmap = Bitmap.createBitmap(bmp, (w - h) / 2, 0, h,
                    h);
        } else {
            squareBitmap = bmp;
        }

        // 对squareBitmap进行缩放为diameter边长的正方形位图
        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledBitmap = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);
        } else {
            scaledBitmap = squareBitmap;
        }

        Bitmap outputBmp = createBitmapSafely(diameter,
                diameter, Bitmap.Config.ARGB_8888,1);
        Canvas canvas = new Canvas(outputBmp);// 创建一个相同大小的画布
        Paint paint = new Paint();// 定义画笔
        paint.setAntiAlias(true);// 设置抗锯齿
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        if ("star".equals(shape_type)) {// 如果绘制的形状为五角星形
            Path path = new Path();
            float radian = degree2Radian(36);// 36为五角星的角度
            float radius_in = (float) (radius * Math.sin(radian / 2) / Math
                    .cos(radian)); // 中间五边形的半径
            path.moveTo((float) (radius * Math.cos(radian / 2)), 0);// 此点为多边形的起点
            float len1 = (float)(radius - radius * Math.sin(radian / 2));
            float len2 = (float) (radius + radius_in * Math.sin(radian / 2));
            float len3 = (float)(radius + radius
                    * Math.cos(radian));
            path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in
                            * Math.sin(radian)),
                    (float) len1);
            path.lineTo((float) (radius * Math.cos(radian / 2) * 2),
                    (float) len1);
            path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in
                            * Math.cos(radian / 2)),
                    (float) len2);

            path.lineTo(
                    (float) (radius * Math.cos(radian / 2) + radius
                            * Math.sin(radian)), (float) len3);
            path.lineTo((float) (radius * Math.cos(radian / 2)),
                    (float) (radius + radius_in));
            path.lineTo(
                    (float) (radius * Math.cos(radian / 2) - radius
                            * Math.sin(radian)), (float) len3);
            path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in
                            * Math.cos(radian / 2)),
                    (float) len2);
            path.lineTo(0, (float) len1);
            path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in
                            * Math.sin(radian)),
                    (float) len1);
            path.close();// 使这些点构成封闭的多边形
            canvas.drawPath(path, paint);
        } else if ("triangle".equals(shape_type)) {// 如果绘制的形状为三角形
            Path path = new Path();
            path.moveTo(0, 0);
            path.lineTo(diameter / 2, diameter);
            path.lineTo(diameter, 0);
            path.close();
            canvas.drawPath(path, paint);
        } else if ("heart".equals(shape_type)) {// 如果绘制的形状为心形
            Path path = new Path();
            path.moveTo(diameter / 2, diameter / 5);
            path.quadTo(diameter, 0, diameter / 2, diameter / 1.0f);
            path.quadTo(0, 0, diameter / 2, diameter / 5);
            path.close();
            canvas.drawPath(path, paint);
        } else {// 这是默认形状，圆形
            // 绘制圆形
            canvas.drawCircle(scaledBitmap.getWidth() / 2,
                    scaledBitmap.getHeight() / 2, scaledBitmap.getWidth() / 2,
                    paint);
        }
        // 设置Xfermode的Mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, 0, 0, paint);

        if(bmp != null && !bmp.isRecycled())
            bmp.recycle();
        if(squareBitmap != null && !squareBitmap.isRecycled())
            squareBitmap.recycle();
        if(scaledBitmap != null && !scaledBitmap.isRecycled())
            scaledBitmap.recycle();

        return outputBmp;
    }

    /**
     * 角度转弧度公式
     * @param degree
     * @return
     */
    private static float degree2Radian(int degree) {
        return (float) (Math.PI * degree / 180.0);
    }

    public static Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHeight){
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        if(screenHeight <= 0 || screenWidth <= 0){
            return bitmap;
        }
        Matrix matrix = new Matrix();
        float widthScale = (float) screenWidth / w;
        float heightScale = (float) screenHeight / h;
        matrix.postScale(widthScale, heightScale);
        Bitmap outputBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        if(bitmap != null && !bitmap.equals(outputBmp) && !bitmap.isRecycled()){
            // 释放与此位图关联的本机对象，并清除对像素数据的引用
            bitmap.recycle();
        }
        return outputBmp;
    }

    public static int dp2Px(int dp) {
        return Math.round(dp * DENSITY);
    }

    public static Bitmap createBitmapFromView(View view) {
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null) {
            // 关键字synchronized可以保证在同一时刻
            // 只有一个线程可以执行某个方法或某个代码块，同时synchronized可以保证一个线程的变化可见
            synchronized (sCanvas) {
                // 将view画到新建的bitmap上
                Canvas canvas = sCanvas;
                canvas.setBitmap(bitmap);
                view.draw(canvas);
                canvas.setBitmap(null);
            }
        }
        return bitmap;
    }

    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                // Runs the garbage collector.
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
            return null;
        }
    }

}


