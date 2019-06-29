package com.easy.tvbox.utils;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easy.tvbox.R;

/**
 * 短信验证码倒计时
 */
public class SMSCountDownTimer extends CountDownTimer {
    View view;//显示倒计时的控件

    String oldText;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public SMSCountDownTimer(View view, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.view = view;
        this.view.setClickable(false);
        this.view.setEnabled(false);
        this.oldText = view.getContext().getString(R.string.get_phone_code);
        isfinish = false;
        start();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        //防止计时过程中重复点击
//        view.setText(millisUntilFinished / 1000 + "s后重新获取");
        ((this.view instanceof TextView ? (TextView) view : (Button) view)).setText(millisUntilFinished / 1000 + "s后重新获取");
    }

    boolean isfinish;

    @Override
    public void onFinish() {
//重新给Button设置文字
//        view.setText("重新获取");
        ((this.view instanceof TextView ? (TextView) view : (Button) view)).setText(oldText);
        //设置可点击
        view.setClickable(true);
        view.setEnabled(true);
        isfinish = true;
    }


    public boolean isFinish() {

        return isfinish;
    }
}
