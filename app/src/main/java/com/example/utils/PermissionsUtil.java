package com.example.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by Tinglan on 2020/9/14 00:03
 * It works!!
 */
public class PermissionsUtil {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String TAG = "PermissionsUtil";

    // 这是要申请的权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void requestPermission(Activity activity) {
        // 版本不低于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                for (String permission : PERMISSIONS) {
                    LogUtil.d(TAG,"ASK FOR PERMISSION "+permission);
                    int permissionNum = activity.checkSelfPermission(permission);
                    if(permissionNum != PackageManager.PERMISSION_GRANTED){
                        activity.requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
        }
    }
}
