package com.volokh.danylo.imagetransition.library.animators;

import android.view.View;

/**
 * Created by danylo.volokh on 3/8/16.
 */
public abstract class TransitionAnimator {

    private long mDuration;

    public TransitionAnimator(long duration) {
        mDuration = duration;
    }

    public abstract void animate(View contentRoot);

    public TransitionAnimator setDuration(int duration){
        mDuration = duration;
        return this;
    }

    protected long getDuration() {
        return mDuration;
    }

    abstract void cancelCurrentAnimation();
}
