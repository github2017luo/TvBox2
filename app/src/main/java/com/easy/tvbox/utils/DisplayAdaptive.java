package com.easy.tvbox.utils;

import android.util.TypedValue;

import com.easy.tvbox.base.App;


public class DisplayAdaptive {
    
    private static class DisplayAdaptiveHolder {
        private static final DisplayAdaptive INSTANCE = new DisplayAdaptive();
    }
    
    private DisplayAdaptive() {}
    
    public static DisplayAdaptive getInstance() {
        return DisplayAdaptiveHolder.INSTANCE;
    }
    
    public float toLocalPx(float pt) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, pt, App.getApp().getResources().getDisplayMetrics());
    }

}
