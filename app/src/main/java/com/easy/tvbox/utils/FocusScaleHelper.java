package com.easy.tvbox.utils;

import android.animation.TimeAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.easy.tvbox.R;

public class FocusScaleHelper {
    /**
     * 是不是有效的放大指数
     *
     * @param zoomIndex
     */
    private static boolean isValidZoomIndex(int zoomIndex) {
        return zoomIndex == FocusHighlight.ZOOM_FACTOR_NONE || getResId(zoomIndex) > 0;
    }

    /**
     * 获得资源ID
     *
     * @param zoomIndex
     */
    private static int getResId(int zoomIndex) {
        switch (zoomIndex) {
            case FocusHighlight.ZOOM_FACTOR_SMALL:
                return R.fraction.focus_zoom_factor_small;
            case FocusHighlight.ZOOM_FACTOR_XSMALL:
                return R.fraction.focus_zoom_factor_xsmall;
            case FocusHighlight.ZOOM_FACTOR_MEDIUM:
                return R.fraction.focus_zoom_factor_medium;
            case FocusHighlight.ZOOM_FACTOR_LARGE:
                return R.fraction.focus_zoom_factor_large;
            default:
                return 0;
        }
    }

    /**
     * item高亮焦点缩放
     */
    public static class ItemFocusScale implements FocusHighlightHandler {
        private static final int DURATION_MS = 150;

        private int mScaleIndex;

        public ItemFocusScale(int zoomIndex) {
            if (!isValidZoomIndex(zoomIndex)) {
                throw new IllegalArgumentException("Unhandled zoom index");
            }
            mScaleIndex = zoomIndex;
        }

        private float getScale(Resources res) {
            return mScaleIndex == FocusHighlight.ZOOM_FACTOR_NONE ? 1f : res.getFraction(getResId(mScaleIndex), 1, 1);
        }

        @Override
        public void onItemFocused(View view, boolean hasFocus) {
            getOrCreateAnimator(view).animateFocus(hasFocus, false);
        }

        @Override
        public void onInitializeView(View view) {
            getOrCreateAnimator(view).animateFocus(false, true);
        }

        private FocusAnimator getOrCreateAnimator(View view) {
//            FocusAnimator animator = (FocusAnimator) view.getTag(R.id.focus_animator);
//            if (animator == null) {
//                animator = new FocusAnimator(view, getScale(view.getResources()), DURATION_MS);
//                view.setTag(R.id.focus_animator, animator);
//            }
//            return animator;
            return null;
        }
    }

    /**
     * 焦点动画
     */
    private static class FocusAnimator implements TimeAnimator.TimeListener {
        private final View mView;
        private final int mDuration;
        private final float mScaleDiff;
        private float mFocusLevel = 0f;
        private float mFocusLevelStart;
        private float mFocusLevelDelta;
        private final TimeAnimator mAnimator = new TimeAnimator();
        private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

        void animateFocus(boolean select, boolean immediate) {
            endAnimation();
            final float end = select ? 1 : 0;
            if (immediate) {
                setFocusLevel(end);
            } else if (mFocusLevel != end) {
                mFocusLevelStart = mFocusLevel;
                mFocusLevelDelta = end - mFocusLevelStart;
                mAnimator.start();
            }
        }

        FocusAnimator(View view, float scale, int duration) {
            mView = view;
            mDuration = duration;
            mScaleDiff = scale - 1f;
            mAnimator.setTimeListener(this);
        }

        void setFocusLevel(float level) {
            mFocusLevel = level;
            float scale = 1f + mScaleDiff * level;
            mView.setScaleX(scale);
            mView.setScaleY(scale);
        }

        float getFocusLevel() {
            return mFocusLevel;
        }

        void endAnimation() {
            mAnimator.end();
        }

        @Override
        public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
            float fraction;
            if (totalTime >= mDuration) {
                fraction = 1;
                mAnimator.end();
            } else {
                fraction = (float) (totalTime / (double) mDuration);
            }
            if (mInterpolator != null) {
                fraction = mInterpolator.getInterpolation(fraction);
            }
            setFocusLevel(mFocusLevelStart + fraction * mFocusLevelDelta);
        }
    }
}
