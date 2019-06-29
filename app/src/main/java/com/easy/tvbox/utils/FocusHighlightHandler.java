package com.easy.tvbox.utils;

import android.view.View;

public interface FocusHighlightHandler {
    void onItemFocused(View view, boolean hasFocus);

    void onInitializeView(View view);
}
