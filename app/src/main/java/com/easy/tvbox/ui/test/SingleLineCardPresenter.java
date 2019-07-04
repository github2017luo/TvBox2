package com.easy.tvbox.ui.test;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.leanback.widget.ImageCardView;

import com.easy.tvbox.R;

public class SingleLineCardPresenter extends ImageCardViewPresenter {

    public SingleLineCardPresenter(Context context) {
        super(context, R.style.SingleLineCardTheme);
    }

    @Override public void onBindViewHolder(Card card, ImageCardView cardView) {
        super.onBindViewHolder(card, cardView);
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(R.styleable.lbImageCardView);
        android.util.Log.d("SHAAN", "lbImageCardViewType ="+typedArray.getInt(R.styleable.lbImageCardView_lbImageCardViewType, -1));
        cardView.setInfoAreaBackgroundColor(card.getFooterColor());
    }

}
