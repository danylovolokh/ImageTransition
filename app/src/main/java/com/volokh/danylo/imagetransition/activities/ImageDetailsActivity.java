package com.volokh.danylo.imagetransition.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.otto.Bus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.event_bus.ChangeImageThumbnailVisibility;
import com.volokh.danylo.imagetransition.event_bus.EventBusCreator;
import com.volokh.danylo.imagetransition.MatrixEvaluator;
import com.volokh.danylo.imagetransition.MatrixUtils;
import com.volokh.danylo.imagetransition.R;
import com.volokh.danylo.imagetransition.library.animators.SimpleAnimationListener;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by danylo.volokh on 2/21/2016.
 *
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

    /**
     * This array will contain the data about image matrix of original image.
     * We will use that matrix to animate image back to original state
     */
    private float[] mInitThumbnailMatrixValues = new float[9];

    private AnimatorSet mEnteringAnimation;
    private AnimatorSet mExitingAnimation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        setContentView(R.layout.image_details_activity_layout);
        mEnlargedImage = (ImageView) findViewById(R.id.enlarged_image);

        mImageDownloader = Picasso.with(this);

        File imageFile = (File) getIntent().getSerializableExtra(IMAGE_FILE_KEY);

        mImageDownloader.load(imageFile).into(mEnlargedImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "onSuccess, mEnlargedImage");

                // in this callback we already have image set into ImageView and we can use it's Matrix for animation
                runEnterAnimationIfNeeded(savedInstanceState);
            }

            @Override
            public void onError() {
                Log.v(TAG, "onError, mEnlargedImage");
            }
        });

        initializeTransitionView();

        initInitialThumbnail();
    }

    private void initializeTransitionView() {
        FrameLayout androidContent = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        mTransitionImage = new ImageView(this);
        androidContent.addView(mTransitionImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // prevent leaking activity if image was not loaded yet
        Picasso.with(this).cancelRequest(mEnlargedImage);
    }

    /**
     * This method runs entering animation if bundle is null -
     * it means that we entered the activity for the first time and not returning to it from recent apps or so.
     */
    private void runEnterAnimationIfNeeded(Bundle savedInstanceState) {
        Log.v(TAG, "runEnterAnimationIfNeeded, savedInstanceState " + savedInstanceState);

        if (savedInstanceState == null) {
            // if savedInstanceState is null activity is started for the first time.
            // run the animation

            mEnlargedImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    // When this method is called we already have everything laid out and measured so we can start our animation
                    Log.v(TAG, "onPreDraw");

                    // start animation only when layout is measured and laid out.
                    mEnlargedImage.getViewTreeObserver().removeOnPreDrawListener(this);
                    playEnteringAnimation(mTransitionImage);

                    return true;
                }
            });
        } else {
            // Activity is retrieved. Main container is invisible. Make it visible
            View mainContainer = findViewById(R.id.main_container);
            mainContainer.setAlpha(1.0f);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * This method initializes image view that will be animated.
     * This ImageView will look like image is transited from previous screen to this one.
     */
    private void initInitialThumbnail() {

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
        mTransitionImage.setVisibility(View.INVISIBLE);
    }

    /**
     * This method combines several animations when screen is opened.
     * 1. Animation of ImageView position and image matrix.
     * 2. Animation of main container elements - they are fading in after image is animated to position
     */

    private void playEnteringAnimation(final ImageView transitionImage) {

        mTransitionImage.setVisibility(View.VISIBLE);

        AnimatorSet imageAnimatorSet = createEnteringImageAnimation(transitionImage);

        Animator mainContainerFadeAnimator = createEnteringFadeAnimator();

        mEnteringAnimation = new AnimatorSet();
        mEnteringAnimation.setDuration(IMAGE_TRANSLATION_DURATION);
        mEnteringAnimation.setInterpolator(new AccelerateInterpolator());
        mEnteringAnimation.addListener(new SimpleAnimationListener(){

            @Override
            public void onAnimationStart(Animator animation) {
                mBus.post(new ChangeImageThumbnailVisibility(false));
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Log.v(TAG, "onAnimationEnd, mEnteringAnimation " + mEnteringAnimation);
                mEnteringAnimation = null;

                mEnlargedImage.setVisibility(View.VISIBLE);
                mTransitionImage.setVisibility(View.INVISIBLE);
            }
        });

        mEnteringAnimation.playTogether(
                imageAnimatorSet,
                mainContainerFadeAnimator
        );

        mEnteringAnimation.start();
    }

    /**
     * Animator returned form this method animates fade in of all other elements on the screen besides ImageView
     */
    private ObjectAnimator createEnteringFadeAnimator() {
        View mainContainer = findViewById(R.id.main_container);
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(mainContainer, "alpha", 0.0f, 1.0f);
        return fadeInAnimator;
    }

    private ObjectAnimator createExitingFadeAnimator() {
        View mainContainer = findViewById(R.id.main_container);
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(mainContainer, "alpha", 1.0f, 0.0f);
        return fadeInAnimator;
    }

    /**
     * This method creates an animator set of 2 animations:
     * 1. ImageView position animation when screen is opened
     * 2. ImageView image matrix animation when screen is opened
     */
    @NonNull
    private AnimatorSet createEnteringImageAnimation(ImageView transitionImage) {
        Log.v(TAG, ">> createEnteringImageAnimation");

        ObjectAnimator positionAnimator = createEnteringImagePositionAnimator(transitionImage);
        ObjectAnimator matrixAnimator = createEnteringImageMatrixAnimator(transitionImage);

        AnimatorSet enteringImageAnimation = new AnimatorSet();
        enteringImageAnimation.playTogether(positionAnimator, matrixAnimator);

        Log.v(TAG, "<< createEnteringImageAnimation");
        return enteringImageAnimation;
    }

    /**
     * This method creates an animator set of 2 animations:
     * 1. ImageView position animation when screen is closed
     * 2. ImageView image matrix animation when screen is closed
     */
    private AnimatorSet createExitingImageAnimation(ImageView transitionImage) {
        Log.v(TAG, ">> createExitingImageAnimation");

        ObjectAnimator positionAnimator = createExitingImagePositionAnimator(transitionImage);
        ObjectAnimator matrixAnimator = createExitingImageMatrixAnimator(transitionImage);

        AnimatorSet exitingImageAnimation = new AnimatorSet();
        exitingImageAnimation.playTogether(positionAnimator, matrixAnimator);

        Log.v(TAG, "<< createExitingImageAnimation");
        return exitingImageAnimation;
    }

    /**
     * This method creates an animator that changes ImageView position on the screen.
     * It will look like view is translated from its position on previous screen to its new position on this screen
     */
    @NonNull
    private ObjectAnimator createEnteringImagePositionAnimator(final ImageView transitionImage) {
        final int[] finalLocationOnTheScreen = new int[2];
        mEnlargedImage.getLocationOnScreen(finalLocationOnTheScreen);

        Log.v(TAG, "createEnteringImagePositionAnimator, finalLocationOnTheScreen " + Arrays.toString(finalLocationOnTheScreen));

        PropertyValuesHolder propertyLeft = PropertyValuesHolder.ofInt("left", transitionImage.getLeft(), finalLocationOnTheScreen[0]);
        PropertyValuesHolder propertyTop = PropertyValuesHolder.ofInt("top", transitionImage.getTop(), finalLocationOnTheScreen[1] - getStatusBarHeight());

        PropertyValuesHolder propertyRight = PropertyValuesHolder.ofInt("right", transitionImage.getRight(), finalLocationOnTheScreen[0] + mEnlargedImage.getWidth());
        PropertyValuesHolder propertyBottom = PropertyValuesHolder.ofInt("bottom", transitionImage.getBottom(), finalLocationOnTheScreen[1] + mEnlargedImage.getHeight() - getStatusBarHeight());

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(transitionImage, propertyLeft, propertyTop, propertyRight, propertyBottom);
        animator.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // set new parameters of animated ImageView. This will prevent blinking of view when set visibility to visible in Exit animation
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) transitionImage.getLayoutParams();
                layoutParams.height = mEnlargedImage.getHeight();
                layoutParams.width = mEnlargedImage.getWidth();
                layoutParams.setMargins(finalLocationOnTheScreen[0], finalLocationOnTheScreen[1] - getStatusBarHeight(), 0, 0);
            }
        });
        return animator;
    }

    /**
     * This method creates an animator that changes ImageView position on the screen.
     * It will look like view is translated from its position on this screen to its position on previous screen
     */
    @NonNull
    private ObjectAnimator createExitingImagePositionAnimator(final ImageView transitionImage) {

        Bundle initialBundle = getIntent().getExtras();
        int initialThumbnailLeft = initialBundle.getInt(KEY_THUMBNAIL_INIT_LEFT_POSITION);

        int initialThumbnailTop = initialBundle.getInt(KEY_THUMBNAIL_INIT_TOP_POSITION);

        int initialThumbnailWidth = initialBundle.getInt(KEY_THUMBNAIL_INIT_WIDTH);
        int initialThumbnailHeight = initialBundle.getInt(KEY_THUMBNAIL_INIT_HEIGHT);

        int[] locationOnScreen = new int[2];
        mEnlargedImage.getLocationOnScreen(locationOnScreen);

        PropertyValuesHolder propertyLeft = PropertyValuesHolder.ofInt("left",
                locationOnScreen[0],
                initialThumbnailLeft);

        PropertyValuesHolder propertyTop = PropertyValuesHolder.ofInt("top",
                locationOnScreen[1] - getStatusBarHeight(),
                initialThumbnailTop - getStatusBarHeight());

        PropertyValuesHolder propertyRight = PropertyValuesHolder.ofInt("right",
                locationOnScreen[0] + mEnlargedImage.getWidth(),
                initialThumbnailLeft + initialThumbnailWidth);


        PropertyValuesHolder propertyBottom = PropertyValuesHolder.ofInt("bottom",
                mEnlargedImage.getBottom(),
                initialThumbnailTop + initialThumbnailHeight - getStatusBarHeight());

        return ObjectAnimator.ofPropertyValuesHolder(transitionImage, propertyLeft, propertyTop, propertyRight, propertyBottom);
    }

    /**
     * This method creates Animator that will animate matrix of ImageView.
     * It is needed in order to show the effect when scaling of one view is smoothly changed to the scale of the second view.
     * <p>
     * For example: first view can have scaleType: centerCrop, and the other one fitCenter.
     * The image inside ImageView will smoothly change from one to another
     */
    private ObjectAnimator createEnteringImageMatrixAnimator(final ImageView transitionImage) {

        Matrix initMatrix = MatrixUtils.getImageMatrix(transitionImage);
        // store the data about original matrix into array.
        // this array will be used later for exit animation
        transitionImage.getImageMatrix().getValues(mInitThumbnailMatrixValues);

        final Matrix endMatrix = MatrixUtils.getImageMatrix(mEnlargedImage);
        Log.v(TAG, "createEnteringImageMatrixAnimator, initMatrix " + initMatrix);
        Log.v(TAG, "createEnteringImageMatrixAnimator,  endMatrix " + endMatrix);

        transitionImage.setScaleType(ImageView.ScaleType.MATRIX);

        return ObjectAnimator.ofObject(transitionImage, MatrixEvaluator.ANIMATED_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), initMatrix, endMatrix);
    }

    /**
     * This method creates animator that animates Matrix of ImageView.
     * It is needed in order to show the effect when scaling of one view is smoothly changed to the scale of the second view.
     *
     * For example: first view can have scaleType: centerCrop, and the other one fitCenter.
     * The image inside ImageView will smoothly change from one to another
     */
    private ObjectAnimator createExitingImageMatrixAnimator(ImageView transitionImage) {

        Matrix initialMatrix = MatrixUtils.getImageMatrix(mEnlargedImage);

        Matrix endMatrix = new Matrix();
        endMatrix.setValues(mInitThumbnailMatrixValues);

        Log.v(TAG, "createExitingImageMatrixAnimator, initialMatrix " + initialMatrix);
        Log.v(TAG, "createExitingImageMatrixAnimator,     endMatrix " + endMatrix);

        transitionImage.setScaleType(ImageView.ScaleType.MATRIX);

        return ObjectAnimator.ofObject(transitionImage, MatrixEvaluator.ANIMATED_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), initialMatrix, endMatrix);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.v(TAG, "onBackPressed, mEnteringAnimation " + mEnteringAnimation);

        if(mEnteringAnimation != null){
            // cancel existing animation
            mEnteringAnimation.cancel();
        }

        Log.v(TAG, "onBackPressed, mExitingAnimation " + mExitingAnimation);

        if(mExitingAnimation == null){
            playExitingAnimation();
        }
    }

    private void playExitingAnimation() {
        Log.v(TAG, "playExitingAnimation");

        mTransitionImage.setVisibility(View.VISIBLE);
        mEnlargedImage.setVisibility(View.INVISIBLE);

        AnimatorSet imageAnimatorSet = createExitingImageAnimation(mTransitionImage);

        Animator mainContainerFadeAnimator = createExitingFadeAnimator();

        mExitingAnimation = new AnimatorSet();
        mExitingAnimation.setDuration(IMAGE_TRANSLATION_DURATION);
        mExitingAnimation.setInterpolator(new AccelerateInterpolator());
        mExitingAnimation.addListener(new SimpleAnimationListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mBus.post(new ChangeImageThumbnailVisibility(true));

                Log.v(TAG, "onAnimationEnd, mExitingAnimation " + mExitingAnimation);
                mExitingAnimation = null;

                // finish the activity when animation is finished
                finish();
                overridePendingTransition(0, 0);
            }
        });

        mExitingAnimation.playTogether(
                imageAnimatorSet,
                mainContainerFadeAnimator
        );

        mExitingAnimation.start();
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


}
