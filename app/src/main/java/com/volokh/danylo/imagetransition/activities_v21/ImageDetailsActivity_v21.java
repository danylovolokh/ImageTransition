package com.volokh.danylo.imagetransition.activities_v21;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.R;

import java.io.File;

/**
 * Created by danylo.volokh on 2/21/2016.
 */
public class ImageDetailsActivity_v21 extends Activity{

    public static final String SHARED_ELEMENT_IMAGE_KEY = "SHARED_ELEMENT_IMAGE_KEY";
    public static final String IMAGE_FILE_KEY = "IMAGE_FILE_KEY";

    private ImageView mImage;

    private Picasso mImageDownloader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_details_activity_layout);

        mImage = (ImageView) findViewById(R.id.enlarged_image);
        mImage.setVisibility(View.VISIBLE);

        String imageTransitionName = getIntent().getStringExtra(SHARED_ELEMENT_IMAGE_KEY);
        ViewCompat.setTransitionName(mImage, imageTransitionName);

        View mainContainer = findViewById(R.id.main_container);
        mainContainer.setAlpha(1.f);
        mImageDownloader = Picasso.with(this);

        File imageFile = (File) getIntent().getSerializableExtra(IMAGE_FILE_KEY);

        mImageDownloader.load(imageFile).into(mImage);

    }

    public static Intent getStartIntent(Activity activity, String sharedImageTransitionName, File imageFile) {
        Intent startIntent = new Intent(activity, ImageDetailsActivity_v21.class);
        startIntent.putExtra(SHARED_ELEMENT_IMAGE_KEY, sharedImageTransitionName);
        startIntent.putExtra(IMAGE_FILE_KEY, imageFile);
        return startIntent;
    }
}
