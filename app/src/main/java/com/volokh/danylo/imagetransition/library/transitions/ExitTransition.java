package com.volokh.danylo.imagetransition.library.transitions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.volokh.danylo.imagetransition.library.OverlayViewGroup;
import com.volokh.danylo.imagetransition.library.Config;
import com.volokh.danylo.imagetransition.library.handler.BaseTransition;
import com.volokh.danylo.imagetransition.library.animators.TransitionAnimator;

/**
 * Created by danylo.volokh on 3/8/16.
 */
public final class ExitTransition extends BaseTransition {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = ExitTransition.class.getSimpleName();

    private Activity mFromActivity;
    private Class mToActivity;

    private TransitionAnimator mExitAnimator;

    private View mExitContentView;

    @Override
    protected void handleActivityCreated(final Activity activity, Bundle savedInstanceState) {

        if (mToActivity == activity.getClass()) {
            if (SHOW_LOGS)
                Log.v(TAG, "handleActivityCreated, savedInstanceState " + savedInstanceState);
            if (savedInstanceState == null) {
                if (SHOW_LOGS)
                    Log.v(TAG, "handleActivityCreated, savedInstanceState null, animate entering");
                final View decorView = activity.getWindow().getDecorView();

                decorView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                    public int mFramesCount;

                    @Override
                    public boolean onPreDraw() {

                        if (SHOW_LOGS)
                            Log.v(TAG, "onPreDraw, handleActivityCreated, mFramesCount " + mFramesCount);
                        FrameLayout contentRoot = (FrameLayout) decorView.findViewById(android.R.id.content);

                        if (mFramesCount++ == 1) {
                            decorView.getViewTreeObserver().removeOnPreDrawListener(this);

                            // add shared elements to overlay view group
                            // set background without shared elements
                            OverlayViewGroup overlayViewGroup = new OverlayViewGroup(activity, contentRoot);
//                                        overlayViewGroup.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_dark));

                            for (View view : getExitTransitionViews().values()) {
                                overlayViewGroup.add(view);
                            }

                            contentRoot.addView(overlayViewGroup);

                        } else {

                            // set background with shared elements
                            ImageView previousActivityBackgroundImage = getBackgroundImageView(activity);

                            if (SHOW_LOGS)
                                Log.v(TAG, "onPreDraw, handleActivityCreated, contentRoot " + contentRoot);
                            contentRoot.addView(previousActivityBackgroundImage);

                            animateExiting(previousActivityBackgroundImage);
                        }

                        return true;
                    }
                });
            }

        }

    }


    void animateExiting(View view) {
        if (SHOW_LOGS) Log.v(TAG, "animateExiting, view " + view);
        if (SHOW_LOGS) Log.v(TAG, "animateExiting, exitAnimator " + mExitAnimator);

        if (mExitAnimator != null) {
            mExitAnimator.animate(view);
        }
    }

    @NonNull
    private ImageView getBackgroundImageView(Activity activity) {
        Bitmap previousActivityBackground = getPreviousActivityBackground(mExitContentView);
        if (SHOW_LOGS)
            Log.v(TAG, "getBackgroundImageView, previousActivityBackground " + previousActivityBackground);
        if (SHOW_LOGS)
            Log.v(TAG, "getBackgroundImageView, height " + previousActivityBackground.getHeight());
        if (SHOW_LOGS)
            Log.v(TAG, "getBackgroundImageView, width " + previousActivityBackground.getWidth());

        ImageView previousActivityBackgroundImage = new ImageView(activity);
        previousActivityBackgroundImage.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_red_dark, null));
        previousActivityBackgroundImage.setImageBitmap(previousActivityBackground);
        return previousActivityBackgroundImage;
    }

    private Bitmap getPreviousActivityBackground(View view) {

//        sContentView.setDrawingCacheEnabled(true);
        Bitmap drawingCache = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(drawingCache);
        view.draw(c);

//        sContentView.setDrawingCacheEnabled(false);

        return drawingCache;
    }

    public ExitTransition fromActivity(Activity activity) {
        mFromActivity = activity;
        mExitContentView = mFromActivity.getWindow().getDecorView().findViewById(android.R.id.content);

        return this;
    }

    public ExitTransition toActivity(Class activity) {
        mToActivity = activity;
        return this;
    }

    public ExitTransition withExitAnimator(TransitionAnimator animator) {
        mExitAnimator = animator;
        return this;
    }
}
