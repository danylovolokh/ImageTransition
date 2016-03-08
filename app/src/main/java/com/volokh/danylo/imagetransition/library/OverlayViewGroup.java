package com.volokh.danylo.imagetransition.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

public class OverlayViewGroup extends ViewGroup {


    private static final String TAG = OverlayViewGroup.class.getSimpleName();
    private final int mRight;
    private final int mBottom;
    /**
     * The View for which this is an overlay. Invalidations of the overlay are redirected to
     * this host view.
     */
    View mHostView;

    /**
     * The set of drawables to draw when the overlay is rendered.
     */
    ArrayList<Drawable> mDrawables = null;

    public OverlayViewGroup(Context context, View hostView) {
        super(context);
        mHostView = hostView;

        mRight = hostView.getWidth();
        mBottom = hostView.getHeight();
    }

    public void add(Drawable drawable) {
        if (mDrawables == null) {

            mDrawables = new ArrayList<>();
        }
        if (!mDrawables.contains(drawable)) {
            // Make each drawable unique in the overlay; can't add it more than once
            mDrawables.add(drawable);
            invalidate(drawable.getBounds());
            drawable.setCallback(this);
        }
    }

    public void remove(Drawable drawable) {
        if (mDrawables != null) {
            mDrawables.remove(drawable);
            invalidate(drawable.getBounds());
            drawable.setCallback(null);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (mDrawables != null && mDrawables.contains(who));
    }

    public void add(View child) {
        Log.v(TAG, "add, child " + child);

        if (child.getParent() instanceof ViewGroup) {

            ViewGroup parent = (ViewGroup) child.getParent();
            Log.v(TAG, "add, parent " + parent);

            if (parent != mHostView && parent.getParent() != null) {
                // Moving to different container; figure out how to position child such that
                // it is in the same location on the screen
                int[] parentLocation = new int[2];
                int[] hostViewLocation = new int[2];
                parent.getLocationOnScreen(parentLocation);
                mHostView.getLocationOnScreen(hostViewLocation);
                child.offsetLeftAndRight(parentLocation[0] - hostViewLocation[0]);
                child.offsetTopAndBottom(parentLocation[1] - hostViewLocation[1]);
            } else {
                Log.v(TAG, "add, not added");
            }
            parent.removeView(child);
        }
        super.addView(child);
    }

    public void remove(View view) {
        super.removeView(view);
    }

    public void clear() {
        removeAllViews();
        if (mDrawables != null) {
            mDrawables.clear();
        }
    }

    boolean isEmpty() {
        return getChildCount() == 0 &&
                (mDrawables == null || mDrawables.size() == 0);
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        invalidate(drawable.getBounds());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        final int numDrawables = (mDrawables == null) ? 0 : mDrawables.size();
        for (int i = 0; i < numDrawables; ++i) {
            mDrawables.get(i).draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Noop: children are positioned absolutely
    }

        /*
         The following invalidation overrides exist for the purpose of redirecting invalidation to
         the host view. The overlay is not parented to the host view (since a View cannot be a
         parent), so the invalidation cannot proceed through the normal parent hierarchy.
         There is a built-in assumption that the overlay exactly covers the host view, therefore
         the invalidation rectangles received do not need to be adjusted when forwarded to
         the host view.
         */

    @Override
    public void invalidate(Rect dirty) {
        super.invalidate(dirty);
        if (mHostView != null) {
            mHostView.invalidate(dirty);
        }
    }

    @Override
    public void invalidate(int l, int t, int r, int b) {
        super.invalidate(l, t, r, b);
        if (mHostView != null) {
            mHostView.invalidate(l, t, r, b);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (mHostView != null) {
            mHostView.invalidate();
        }
    }


    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        if (mHostView != null) {
            dirty.offset(location[0], location[1]);
            if (mHostView instanceof ViewGroup) {
                location[0] = 0;
                location[1] = 0;
                super.invalidateChildInParent(location, dirty);
                return ((ViewGroup) mHostView).invalidateChildInParent(location, dirty);
            } else {
                invalidate(dirty);
            }
        }
        return null;
    }
}
