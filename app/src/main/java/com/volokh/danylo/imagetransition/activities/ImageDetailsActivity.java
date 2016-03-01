package com.volokh.danylo.imagetransition.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.R;

import java.io.File;

/**
 * Created by danylo.volokh on 2/21/2016.
 */
public class ImageDetailsActivity extends Activity{

    public static final String SHARED_ELEMENT_IMAGE_KEY = "SHARED_ELEMENT_IMAGE_KEY";
    public static final String IMAGE_FILE_KEY = "IMAGE_FILE_KEY";

    private TextView mImageDescription;
    private ImageView mImage;

    private Picasso mImageDownloader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);

        setContentView(R.layout.user_accout_activity_layout);
        mImage = (ImageView) findViewById(R.id.enlarged_image);
        mImageDescription = (TextView) findViewById(R.id.image_description);
        String imageTransitionName = getIntent().getStringExtra(SHARED_ELEMENT_IMAGE_KEY);

        ViewCompat.setTransitionName(mImage, imageTransitionName);

        mImageDownloader = Picasso.with(this);

        File imageFile = (File) getIntent().getSerializableExtra(IMAGE_FILE_KEY);

        mImageDownloader.load(imageFile).into(mImage);

        final View rootView = ImagesListActivity.getRootView();

        View decorView = getWindow().getDecorView();
        final FrameLayout contentView = (FrameLayout) decorView.findViewById(android.R.id.content);
        contentView.addView(rootView);

        contentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                contentView.getViewTreeObserver().removeOnPreDrawListener(this);

                Animator fadeOutAnimator = ObjectAnimator.ofFloat(rootView, "alpha", 1f, 0f);
                fadeOutAnimator.setDuration(5000);
                fadeOutAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        contentView.removeView(rootView);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                fadeOutAnimator.start();
                return false;
            }
        });

    }

    public static Intent getStartIntent(Activity activity, String sharedImageTransitionName, File imageFile) {
        Intent startIntent = new Intent(activity, ImageDetailsActivity.class);
        startIntent.putExtra(SHARED_ELEMENT_IMAGE_KEY, sharedImageTransitionName);
        startIntent.putExtra(IMAGE_FILE_KEY, imageFile);
        return startIntent;
    }
}
