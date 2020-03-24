package com.easy.tvbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;

public class ShutdownBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //处理点击电源键（相当于熄屏，长按才是关机），屏幕熄灭后需要退出登陆
        Log.d("DataChangeReceiver", "已熄屏");
        DataManager.getInstance().deleteAccount();
        RouteManager.goLoginActivity(context);
        EventBus.getDefault().post(new LogoutEvent());
        Log.d("DataChangeReceiver", "注销成功");
    }
}
