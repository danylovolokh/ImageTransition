package com.volokh.danylo.imagetransition.library.animators;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.volokh.danylo.imagetransition.library.Config;

/**
 * Created by danylo.volokh on 3/10/16.
 */
public abstract class BaseImageTransitionAnimator extends SharedElementTransitionAnimator{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = BaseImageTransitionAnimator.class.getSimpleName();

    public BaseImageTransitionAnimator(long duration) {
        super(duration);
    }

    @Override
    public final void runSharedElementTransitionAnimation(View fromView, View toView) {
        if(SHOW_LOGS) Log.v(TAG, "runSharedElementTransitionAnimation, fromView " + fromView + ", toView " + toView);
        if(fromView instanceof ImageView && toView instanceof ImageView){
            runSharedImageTransitionAnimation((ImageView) fromView, (ImageView) toView);
        } else {
            throw new RuntimeException("runSharedElementTransitionAnimation, ImageTransitionAnimator is used for not ImageView");
            // TODO : define what to do here?
        }
    }

    protected abstract void runSharedImageTransitionAnimation(ImageView fromView, ImageView toView);

}
