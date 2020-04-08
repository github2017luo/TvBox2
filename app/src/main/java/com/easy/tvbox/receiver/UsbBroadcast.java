package com.easy.tvbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
//        ToastUtils.showShort("监测到U盘拔出");
    }

    public static void handlerPush(Context context) {
//        ToastUtils.showShort("监测到U盘插入");
    }
}
