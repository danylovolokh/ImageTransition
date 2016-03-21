package com.volokh.danylo.imagetransition.activities;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.otto.Bus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.animations.EnterScreenAnimations;
import com.volokh.danylo.imagetransition.animations.ExitScreenAnimations;
import com.volokh.danylo.imagetransition.event_bus.ChangeImageThumbnailVisibility;
import com.volokh.danylo.imagetransition.event_bus.EventBusCreator;
import com.volokh.danylo.imagetransition.R;

import java.io.File;

/**
 * Created by danylo.volokh on 2/21/2016.
 */
public class ImageDetailsActivity extends Activity {

    private static final String IMAGE_FILE_KEY = "IMAGE_FILE_KEY";
    private static final String KEY_THUMBNAIL_INIT_TOP_POSITION = "KEY_THUMBNAIL_INIT_TOP_POSITION";
    private static final String KEY_THUMBNAIL_INIT_LEFT_POSITION = "KEY_THUMBNAIL_INIT_LEFT_POSITION";
    private static final String KEY_THUMBNAIL_INIT_WIDTH = "KEY_THUMBNAIL_INIT_WIDTH";
    private static final String KEY_THUMBNAIL_INIT_HEIGHT = "KEY_THUMBNAIL_INIT_HEIGHT";
    private static final String KEY_SCALE_TYPE = "KEY_SCALE_TYPE";

    private static final String TAG = ImageDetailsActivity.class.getSimpleName();

    private static final long IMAGE_TRANSLATION_DURATION = 3000;

    private ImageView mEnlargedImage;
    private ImageView mTransitionImage;

    private Picasso mImageDownloader;

    private final Bus mBus = EventBusCreator.defaultEventBus();

    private AnimatorSet mExitingAnimation;

    private EnterScreenAnimations mEnterScreenAnimations;
    private ExitScreenAnimations mExitScreenAnimations;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        setContentView(R.layout.image_details_activity_layout);
        mEnlargedImage = (ImageView) findViewById(R.id.enlarged_image);

        mImageDownloader = Picasso.with(this);

        File imageFile = (File) getIntent().getSerializableExtra(IMAGE_FILE_KEY);

        final View mainContainer = findViewById(R.id.main_container);

        if(savedInstanceState == null){
            // We entered activity for the first time.
            // Initialize Image view that will be transitioned
            initializeTransitionView();
        } else {
            // Activity is retrieved. Main container is invisible. Make it visible
            mainContainer.setAlpha(1.0f);
        }

        mEnterScreenAnimations = new EnterScreenAnimations(mTransitionImage, mEnlargedImage, mainContainer);
        mExitScreenAnimations = new ExitScreenAnimations(mTransitionImage, mEnlargedImage, mainContainer);

        initializeEnlargedImageAndRunAnimation(savedInstanceState, imageFile);
    }

    /**
     * This method waits for the main "big" image is loaded.
     * And then if activity is started for the first time - it runs "entering animation"
     *
     * Activity is entered fro the first time if saveInstanceState is null
     *
     */

    private void initializeEnlargedImageAndRunAnimation(final Bundle savedInstanceState, File imageFile) {
        Log.v(TAG, "initializeEnlargedImageAndRunAnimation");

        mImageDownloader.load(imageFile).into(mEnlargedImage, new Callback() {

            /**
             * Image is loaded when this method is called
             */
            @Override
            public void onSuccess() {
                Log.v(TAG, "onSuccess, mEnlargedImage");

                // In this callback we already have image set into ImageView and we can use it's Matrix for animation
                // But we have to wait for final measurements. We use OnPreDrawListener to be sure everything is measured

                if (savedInstanceState == null) {
                    // if savedInstanceState is null activity is started for the first time.
                    // run the animation
                    runEnteringAnimation();
                } else {
                    // activity was retrieved from recent apps. No animation needed, just load the image
                }
            }

            @Override
            public void onError() {
                // CAUTION: on error is not handled. If OutOfMemory emerged during image loading we have to handle it here
                Log.v(TAG, "onError, mEnlargedImage");
            }
        });
    }

    /**
     * This method does very tricky part:
     *
     * It sets up {@link android.view.ViewTreeObserver.OnPreDrawListener}
     * When onPreDraw() method is called the layout is already measured.
     * It means that we can use locations of images on the screen at tis point.
     *
     * 1. When first frame is rendered we start animation.
     * 2. We just let second frame to render
     * 3. Make a view on the previous screen invisible and remove onPreDrawListener
     *
     * Why do we do that:
     * The Android rendering system is double-buffered.
     * Similar technique is used in the SDK. See here : {@link android.app.EnterTransitionCoordinator#startSharedElementTransition}
     *
     * You can read more about it here : https://source.android.com/devices/graphics/architecture.html
     *
     */
    private void runEnteringAnimation() {
        Log.v(TAG, "runEnteringAnimation, addOnPreDrawListener");

        mEnlargedImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            int mFrames = 0;

            @Override
            public boolean onPreDraw() {
                // When this method is called we already have everything laid out and measured so we can start our animation
                Log.v(TAG, "onPreDraw, mFrames " + mFrames);

                switch (mFrames++) {
                    case 0:
                        /**
                         * 1. start animation on first frame
                         */
                        final int[] finalLocationOnTheScreen = new int[2];
                        mEnlargedImage.getLocationOnScreen(finalLocationOnTheScreen);

                        mEnterScreenAnimations.playEnteringAnimation(
                                finalLocationOnTheScreen[0], // left
                                finalLocationOnTheScreen[1], // top
                                mEnlargedImage.getWidth(),
                                mEnlargedImage.getHeight());

                        return true;
                    case 1:
                        /**
                         * 2. Do nothing. We just draw this frame
                         */

                        return true;
                }
                /**
                 * 3.
                 * Make view on previous screen invisible on after this drawing frame
                 * Here we ensure that animated view will be visible when we make the viw behind invisible
                 */
                Log.v(TAG, "run, onAnimationStart");
                mBus.post(new ChangeImageThumbnailVisibility(false));

                mEnlargedImage.getViewTreeObserver().removeOnPreDrawListener(this);

                Log.v(TAG, "onPreDraw, << mFrames " + mFrames);

                return true;
            }
        });
    }


    private void initializeTransitionView() {
        Log.v(TAG, "initializeTransitionView");

        FrameLayout androidContent = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        mTransitionImage = new ImageView(this);
        androidContent.addView(mTransitionImage);

        Bundle bundle = getIntent().getExtras();

        int thumbnailTop = bundle.getInt(KEY_THUMBNAIL_INIT_TOP_POSITION)
                - getStatusBarHeight();
        int thumbnailLeft = bundle.getInt(KEY_THUMBNAIL_INIT_LEFT_POSITION);
        int thumbnailWidth = bundle.getInt(KEY_THUMBNAIL_INIT_WIDTH);

        int thumbnailHeight = bundle.getInt(KEY_THUMBNAIL_INIT_HEIGHT);

        ImageView.ScaleType scaleType = (ImageView.ScaleType) bundle.getSerializable(KEY_SCALE_TYPE);

        Log.v(TAG, "initInitialThumbnail, thumbnailTop [" + thumbnailTop + "]");
        Log.v(TAG, "initInitialThumbnail, thumbnailLeft [" + thumbnailLeft + "]");
        Log.v(TAG, "initInitialThumbnail, thumbnailWidth [" + thumbnailWidth + "]");
        Log.v(TAG, "initInitialThumbnail, thumbnailHeight [" + thumbnailHeight + "]");
        Log.v(TAG, "initInitialThumbnail, scaleType " + scaleType);

        // We set initial margins to the view so that it was situated at exact same spot that view from the previous screen were.
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTransitionImage.getLayoutParams();
        layoutParams.height = thumbnailHeight;
        layoutParams.width = thumbnailWidth;
        layoutParams.setMargins(thumbnailLeft, thumbnailTop, 0, 0);

        File imageFile = (File) getIntent().getSerializableExtra(IMAGE_FILE_KEY);
        mTransitionImage.setScaleType(scaleType);

        mImageDownloader.load(imageFile).noFade().into(mTransitionImage);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // prevent leaking activity if image was not loaded yet
        Picasso.with(this).cancelRequest(mEnlargedImage);
    }

    @Override
    public void onBackPressed() {
//        We don't call super to leave this activity on the screen when back is pressed
//        super.onBackPressed();

        Log.v(TAG, "onBackPressed");

        mEnterScreenAnimations.cancelRunningAnimations();

        Log.v(TAG, "onBackPressed, mExitingAnimation " + mExitingAnimation);

        Bundle initialBundle = getIntent().getExtras();
        int toTop = initialBundle.getInt(KEY_THUMBNAIL_INIT_TOP_POSITION);
        int toLeft = initialBundle.getInt(KEY_THUMBNAIL_INIT_LEFT_POSITION);
        int toWidth = initialBundle.getInt(KEY_THUMBNAIL_INIT_WIDTH);
        int toHeight = initialBundle.getInt(KEY_THUMBNAIL_INIT_HEIGHT);

        mExitScreenAnimations.playExitAnimations(
                toTop,
                toLeft,
                toWidth,
                toHeight,
                mEnterScreenAnimations.getInitialThumbnailMatrixValues());
    }

    public static Intent getStartIntent(Activity activity, File imageFile, int left, int top, int width, int height, ImageView.ScaleType scaleType) {
        Log.v(TAG, "getStartIntent, imageFile " + imageFile);

        Intent startIntent = new Intent(activity, ImageDetailsActivity.class);
        startIntent.putExtra(IMAGE_FILE_KEY, imageFile);

        startIntent.putExtra(KEY_THUMBNAIL_INIT_TOP_POSITION, top);
        startIntent.putExtra(KEY_THUMBNAIL_INIT_LEFT_POSITION, left);
        startIntent.putExtra(KEY_THUMBNAIL_INIT_WIDTH, width);
        startIntent.putExtra(KEY_THUMBNAIL_INIT_HEIGHT, height);
        startIntent.putExtra(KEY_SCALE_TYPE, scaleType);

        return startIntent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

    }
}
