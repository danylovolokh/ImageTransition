package com.volokh.danylo.imagetransition.library.animators;

import android.view.View;

/**
 * Created by danylo.volokh on 3/10/16.
 */
public abstract class ViewTransitionAnimator extends TransitionAnimator {

    public abstract void animate(View contentRoot, TransitionAnimatorListener listener);

    public ViewTransitionAnimator(long duration) {
        super(duration);
    }
}
