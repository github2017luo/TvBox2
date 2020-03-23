package com.easy.tvbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.easy.tvbox.base.DataManager;
import com.easy.tvbox.base.RouteManager;
import com.easy.tvbox.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

public class DataChangeReceiver extends BroadcastReceiver {

    int loginOutTime = 1;//24小时制

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.d("DataChangeReceiver", "hour=" + hour);
        if (hour == loginOutTime) {
            DataManager.getInstance().deleteAccount();
            RouteManager.goLoginActivity(context);
            EventBus.getDefault().post(new LogoutEvent());
            Log.d("DataChangeReceiver", "注销成功");
        }
    }
}
