package com.volokh.danylo.imagetransition.library.animators;

import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by danylo.volokh on 3/8/16.
 */
public abstract class TransitionAnimator {

    private final long mDuration;
    private Interpolator mInterpolator;

    public interface TransitionAnimatorListener{
        void onAnimationEnd();
    }

    public TransitionAnimator(long duration) {
        mDuration = duration;
    }

    protected long getDuration() {
        return mDuration;
    }

    protected TransitionAnimator setInterpolator(Interpolator interpolator){
        mInterpolator = interpolator;
        return this;
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    abstract void cancelCurrentAnimation();
}
