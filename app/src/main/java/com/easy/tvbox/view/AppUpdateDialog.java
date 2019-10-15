package com.easy.tvbox.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easy.tvbox.R;


public class AppUpdateDialog extends Dialog {
    View.OnClickListener listener;
    ProgressBar progressBar;
    TextView tvProgress;

    public AppUpdateDialog(Context context) {
        super(context);
        initDialog();
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.app_update_dialog, null);
        setContentView(view);
        initView(view);
    }

    private void initView(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        tvProgress = view.findViewById(R.id.tvProgress);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (display.getWidth()); //设置宽度
        getWindow().setAttributes(lp);

        Window dialogWindow = getWindow();
        ColorDrawable dw = new ColorDrawable(0x0000ff00);
        dialogWindow.setBackgroundDrawable(dw);
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation_NEW);
    }

    public void setProgress(int aLong) {
        progressBar.setProgress(aLong);
        tvProgress.setText(aLong + "%");
    }
}
