package com.easy.tvbox.ui.test;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.TextureView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.easy.tvbox.base.Constant;

import org.greenrobot.essentials.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Will read the content from a given {@link InputStream} and return it as a {@link String}.
     *
     * @param inputStream The {@link InputStream} which should be read.
     * @return Returns <code>null</code> if the the {@link InputStream} could not be read. Else
     * returns the content of the {@link InputStream} as {@link String}.
     */
    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }

    public static Uri getResourceUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    public static int formatInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * @param @param  type 图片为 pic 应用为 app
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: getSaveFilePath
     * @Description:
     */
    public static String getSaveFilePath(int saveType, Activity activity) {
        String filePath = null;
        switch (saveType) {
            case Constant.TYPE_APP:
                filePath = getRootFilePath(activity) + Constant.SAVE_APP_PATH;
                break;
            case Constant.TYPE_AUDIO_RECORD:
                filePath = getRootFilePath(activity) + Constant.SAVE_AUDIO_RECORD_PATH;
                break;
            case Constant.TYPE_PHOTO:
                filePath = getRootFilePath(activity) + Constant.SAVE_PHOTO_PATH;
                break;
        }
        if (!TextUtils.isEmpty(filePath)) {
            createDirectory(filePath);
        }
        return filePath;
    }

    public static boolean isCanUseSD() {
        try {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        } catch (Exception e) {
//            Logger.i("sdcard不可用");
        }
        return false;
    }

    /**
     * 获取文件保存根目录
     *
     * @return 如果有sdcard 则返回sdcard根路径，
     * 如果没有则返回应用包下file目录/data/data/包名/file/
     */
    public static String getRootFilePath(Activity activity) {
        String strPathHead;
        if (isCanUseSD()) {
            strPathHead = Environment.getExternalStorageDirectory().toString() + File.separator;
        } else {
            strPathHead = activity.getFilesDir().getPath() + File.separator;
        }
        return strPathHead;
    }

    /**
     * 创建文件目录
     *
     * @param filePath
     * @return
     */
    public static boolean createDirectory(String filePath) {
        if (fileIsExist(filePath)) {
            return true;
        } else {
            File file = new File(filePath);
            return file.mkdirs();
        }
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean fileIsExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 记得关闭app，不然华为机子安装后不会出现确认安装完成的页面，导致用户感觉是闪退
     *
     * @param context
     * @param packageName
     * @param filePath
     */
    public static void install(Context context, String packageName, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File apkFile = new File(filePath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, "com.easy.tvbox.provider", apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
