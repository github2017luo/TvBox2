package com.easy.tvbox.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * 调用系统自带页面
 */
public class SystemUtils {

    /**
     * @param version 2.1.3==>20103 2.30.59==>23059
     * @return
     */
    public static int getVersion(String version) {
        int code = 0;
        if (version != null && version.contains(".")) {
            String[] versions = version.split("\\.");
            if (versions.length == 3) {
                code += Integer.valueOf(versions[0]) * 10000;
                code += Integer.valueOf(versions[1]) * 100;
                code += Integer.valueOf(versions[2]);
            }
        }
        return code;
    }
    /**
     * 应用是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppExist(Context context, String packageName) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            mPackageManager.getApplicationInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据包名打开应用
     *
     * @param context     上下文
     * @param packageName 将要打开的应用包名
     */
    public static void openApp(Context context, String packageName) {
        PackageManager mPackageManager = context.getPackageManager();
        Intent intent = mPackageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }
}
