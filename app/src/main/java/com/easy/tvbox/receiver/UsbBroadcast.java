package com.easy.tvbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.easy.tvbox.event.StorageChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class UsbBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case Intent.ACTION_MEDIA_MOUNTED: {
                handlerPush(context);
                break;
            }
            case Intent.ACTION_MEDIA_UNMOUNTED: {
                handlerPull(context);
                break;
            }
            default:
                break;
        }
    }

    public static void handlerPull(Context context) {
        EventBus.getDefault().post("U盘退出");
    }

    public static void handlerPush(Context context) {
        StringBuilder builder = new StringBuilder();
        builder.append("u 盘插入");
        builder.append("\n");
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class storeManagerClazz = storageManager.getClass();

            Method getVolumesMethod = storeManagerClazz.getMethod("getVolumes");

            List<?> volumeInfos = (List<?>) getVolumesMethod.invoke(storageManager);

            Class volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");

            Method getTypeMethod = volumeInfoClazz.getMethod("getType");
            Method getFsUuidMethod = volumeInfoClazz.getMethod("getFsUuid");

            Field fsTypeField = volumeInfoClazz.getDeclaredField("fsType");
            Field fsLabelField = volumeInfoClazz.getDeclaredField("fsLabel");
            Field pathField = volumeInfoClazz.getDeclaredField("path");
            Field internalPath = volumeInfoClazz.getDeclaredField("internalPath");

            if (volumeInfos != null) {

                for (Object volumeInfo : volumeInfos) {
                    int uType = (int) getTypeMethod.invoke(volumeInfo);
                    builder.append("getType：").append(uType).append(" ");
                    String uuid = (String) getFsUuidMethod.invoke(volumeInfo);
                    builder.append("getFsUuid：").append(uuid).append(" ");
                    if (uuid != null) {
                        String fsTypeString = (String) fsTypeField.get(volumeInfo);//U盘类型
                        builder.append("fsType：").append(fsTypeString).append(" ");
                        String fsLabelString = (String) fsLabelField.get(volumeInfo);//U盘名
                        builder.append("fsLabel：").append(fsLabelString).append(" ");
                        String pathString = (String) pathField.get(volumeInfo);//U盘路径
                        builder.append("path：").append(pathString).append(" ");
                        String internalPathString = (String) internalPath.get(volumeInfo);
                        builder.append("internalPath：").append(internalPathString).append(" ");
                        StatFs statFs = new StatFs(internalPathString);
                        long avaibleSize = statFs.getAvailableBytes();//获取U盘可用空间
                        builder.append("AvailableBytes：").append(avaibleSize).append(" ");
                        long totalSize = statFs.getTotalBytes();//获取U盘总空间
                        builder.append("TotalBytes：").append(totalSize).append(" ");
                    }
                    builder.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new StorageChangeEvent(builder.toString()));
    }

}
