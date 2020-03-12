package com.easy.tvbox.base;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.easy.tvbox.R;
import com.owen.focus.FocusBorder;

public abstract class TvBaseFragment<M extends ViewDataBinding> extends BaseFragment<M>{

    protected FocusBorder mFocusBorder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFocusBorder = new FocusBorder.Builder()
                .asColor()
                .borderColorRes(R.color.touming)
                .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 0.001f)
                .shadowColorRes(R.color.touming)
                .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 0.001f)
                .build(this);
    }

    protected void onMoveFocusBorder(View focusedView, float scale) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale));
        }
    }

    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
        }
    }
}
