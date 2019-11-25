package com.easy.aliplayer.view.gesturedialog;

import android.app.Activity;
import android.util.Log;

import com.aliyun.utils.VcPlayerLog;
import com.easy.aliplayer.R;

/**
 * 手势滑动的音量提示框。
 */
public class VolumeDialog extends BaseGestureDialog {

    private static final String TAG = VolumeDialog.class.getSimpleName();
    private float initVolume;

    public VolumeDialog(Activity context, float percent) {
        super(context);
        initVolume = percent;
        mImageView.setImageResource(R.drawable.alivc_volume_img);
        updateVolume(percent);
    }

    /**
     * 更新音量值
     *
     * @param percent 音量百分比
     */
    public void updateVolume(float percent) {
        mTextView.setText((int) percent + "%");
        mImageView.setImageLevel((int) percent);
    }

    /**
     * 获取最后的音量
     *
     * @param changePercent 变化的百分比
     * @return 最后的音量
     */
    public static float getTargetVolume(float initVolume, int changePercent) {
        Log.d(TAG, "changePercent = " + changePercent + " , initVolume  = " + initVolume);
        float newVolume = initVolume - changePercent;
        if (newVolume > 100) {
            newVolume = 100;
        } else if (newVolume < 0) {
            newVolume = 0;
        }
        return newVolume;
    }
}
