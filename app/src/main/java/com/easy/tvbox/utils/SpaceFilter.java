package com.easy.tvbox.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class SpaceFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        if (charSequence.equals(" ")) {
            return "";
        }
        return charSequence;
    }
}
