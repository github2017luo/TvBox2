package com.easy.aliplayer.view.gesture;

import android.app.Activity;
import android.view.View;

import com.easy.aliplayer.view.gesturedialog.BrightnessDialog;
import com.easy.aliplayer.view.gesturedialog.SeekDialog;
import com.easy.aliplayer.view.gesturedialog.VolumeDialog;


/**
 * 手势对话框的管理器。
 * 用于管理亮度{@link BrightnessDialog} ，seek{@link SeekDialog} ，音量{@link VolumeDialog}等dialog的显示/隐藏等。
 */

public class GestureDialogManager {

    //用于构建手势用的dialog
    private Activity mActivity;
    //seek手势对话框
    private SeekDialog mSeekDialog = null;
    //亮度对话框
    private BrightnessDialog mBrightnessDialog = null;
    //音量对话框
    private VolumeDialog mVolumeDialog = null;

    public GestureDialogManager(Activity activity) {
        mActivity = activity;
    }

    /**
     * 显示seek对话框
     *
     * @param parent         显示在哪个view的中间
     * @param targetPosition seek的位置
     */
    public void showSeekDialog(View parent, int targetPosition) {
        if (mSeekDialog == null) {
            mSeekDialog = new SeekDialog(mActivity, targetPosition);
        }
        if (!mSeekDialog.isShowing()) {
            mSeekDialog.show(parent);
        } else {
            mSeekDialog.updatePosition(targetPosition);
        }
    }

    /**
     * 隐藏seek对话框
     */
    public void dismissSeekDialog() {
        if (mSeekDialog != null && mSeekDialog.isShowing()) {
            mSeekDialog.dismiss();
        }
        mSeekDialog = null;
    }

    /**
     * 显示亮度对话框
     *
     * @param parent 显示在哪个view中间
     */
    public void showBrightnessDialog(View parent, int currentBrightness) {
        if (mBrightnessDialog == null) {
            mBrightnessDialog = new BrightnessDialog(mActivity, currentBrightness);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show(parent);
        } else {
            mBrightnessDialog.updateBrightness(currentBrightness);
        }
    }

    /**
     * 隐藏亮度对话框
     */
    public void dismissBrightnessDialog() {
        if (mBrightnessDialog != null && mBrightnessDialog.isShowing()) {
            mBrightnessDialog.dismiss();
        }
        mBrightnessDialog = null;
    }

    /**
     * 显示音量对话框
     *
     * @param parent         显示在哪个view中间
     * @param currentPercent 当前音量百分比
     */
    public void showVolumeDialog(View parent, float currentPercent) {
        if (mVolumeDialog == null) {
            mVolumeDialog = new VolumeDialog(mActivity, currentPercent);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show(parent);
        } else {
            mVolumeDialog.updateVolume(currentPercent);
        }
    }

    /**
     * 关闭音量对话框
     */
    public void dismissVolumeDialog() {
        if (mVolumeDialog != null && mVolumeDialog.isShowing()) {
            mVolumeDialog.dismiss();
        }
        mVolumeDialog = null;
    }
}
