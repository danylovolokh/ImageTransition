package com.volokh.danylo.imagetransition.animations;

import android.content.Context;

/**
 * Created by danylo.volokh on 3/19/16.
 */
public abstract class ScreenAnimation {

    protected static final long IMAGE_TRANSLATION_DURATION = 1000;

    private final Context mContext;

    protected ScreenAnimation(Context context) {
        mContext = context;
    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
