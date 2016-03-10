package com.volokh.danylo.imagetransition.library.animators;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.media.Image;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.volokh.danylo.imagetransition.library.Config;

import java.util.Arrays;

/**
 * Created by danylo.volokh on 3/10/16.
 */
public class ImageTransitionAnimator extends BaseImageTransitionAnimator {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = ImageTransitionAnimator.class.getSimpleName();

    // todo move to base class
    private Animator mImageTransitionAnimator;

    private ImageView mFromImage;
    private ImageView mToImage;

    public ImageTransitionAnimator(long duration) {
        super(duration);
    }

    @Override
    protected void runSharedImageTransitionAnimation(ImageView fromView, ImageView toView) {

        mImageTransitionAnimator = createEnteringImageAnimation(fromView, toView);
        mImageTransitionAnimator.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {

                mImageTransitionAnimator = null;
            }
        });
        mImageTransitionAnimator.start();
    }

    @Override
    void cancelCurrentAnimation() {
        // todo generalize
        if (mImageTransitionAnimator != null) {
            mImageTransitionAnimator.cancel();
        }
    }

    /**
     * This method creates an animator set of 2 animations:
     * 1. ImageView position animation when screen is opened
     * 2. ImageView image matrix animation when screen is opened
     */
    @NonNull
    private Animator createEnteringImageAnimation(ImageView fromView, ImageView toView) {
        if (SHOW_LOGS) Log.v(TAG, ">> createEnteringImageAnimation");

        ObjectAnimator positionAnimator = createEnteringImagePositionAnimator(fromView, toView);

        ObjectAnimator matrixAnimator = createEnteringImageMatrixAnimator(fromView, toView);

        AnimatorSet imageAnimatorSet = new AnimatorSet();
        imageAnimatorSet.playTogether(positionAnimator);

        Interpolator interpolator = getInterpolator();
        if(interpolator != null){
            imageAnimatorSet.setInterpolator(interpolator);
        }

        imageAnimatorSet.setDuration(getDuration());
        return imageAnimatorSet;
    }

    private ObjectAnimator createEnteringImageMatrixAnimator(ImageView fromView, ImageView toView) {
        return null;
    }

    private ObjectAnimator createEnteringImagePositionAnimator(final ImageView fromView, final ImageView toView) {
        final int[] finalLocationOnTheScreen = new int[2];
        toView.getLocationOnScreen(finalLocationOnTheScreen);

        if (SHOW_LOGS)
            Log.v(TAG, "createEnteringImagePositionAnimator, finalLocationOnTheScreen " + Arrays.toString(finalLocationOnTheScreen));

        PropertyValuesHolder propertyLeft = PropertyValuesHolder.ofInt("left", fromView.getLeft(), finalLocationOnTheScreen[0]);
        PropertyValuesHolder propertyTop = PropertyValuesHolder.ofInt("top", fromView.getTop(), finalLocationOnTheScreen[1]
                /*- getStatusBarHeight()*/);

        PropertyValuesHolder propertyRight = PropertyValuesHolder.ofInt("right", fromView.getRight(), finalLocationOnTheScreen[0] + toView.getWidth());
        PropertyValuesHolder propertyBottom = PropertyValuesHolder.ofInt("bottom", fromView.getBottom(), finalLocationOnTheScreen[1] + toView.getHeight()
                /*- getStatusBarHeight()*/);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fromView, propertyLeft, propertyTop, propertyRight, propertyBottom);
        animator.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {

                // set new parameters of animated ImageView. This will prevent blinking of view when set visibility to visible in Exit animation
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fromView.getLayoutParams();
                layoutParams.height = toView.getHeight();
                layoutParams.width = toView.getWidth();
                layoutParams.setMargins(finalLocationOnTheScreen[0], finalLocationOnTheScreen[1]
                        /*- getStatusBarHeight()*/, 0, 0);
            }
        });
        return animator;
    }
}
