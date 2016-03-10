package com.volokh.danylo.imagetransition.library.animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by danylo.volokh on 3/8/16.
 */
public class FadeOutAnimator extends ViewTransitionAnimator {

    private ObjectAnimator mFadeAnimator;

    public FadeOutAnimator(long duration) {
        super(duration);
    }

    @Override
    public void animate(View view, final TransitionAnimatorListener listener) {
        mFadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.f, 0.f);
        mFadeAnimator.addListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onAnimationEnd();
                mFadeAnimator = null;
            }
        });
        mFadeAnimator.setDuration(getDuration());
        mFadeAnimator.start();
    }

    @Override
    public void cancelCurrentAnimation(){
        if(mFadeAnimator != null){
            mFadeAnimator.cancel();
        }
    }
}
