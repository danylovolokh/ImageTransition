package com.volokh.danylo.imagetransition.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.event_bus.EventBusCreator;
import com.volokh.danylo.imagetransition.ImageFilesCreateLoader;
import com.volokh.danylo.imagetransition.adapter.ImagesAdapter;
import com.volokh.danylo.imagetransition.activities_v21.ImagesListActivity_v21;
import com.volokh.danylo.imagetransition.R;
import com.volokh.danylo.imagetransition.event_bus.ChangeImageThumbnailVisibility;
import com.volokh.danylo.imagetransition.adapter.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesListActivity extends Activity implements ImagesAdapter.ImagesAdapterCallback {

    private static final String TAG = ImagesListActivity.class.getSimpleName();

    private static final String IMAGE_DETAILS_IMAGE_MODEL = "IMAGE_DETAILS_IMAGE_MODEL";

    private final List<Image> mImagesList = new ArrayList<>();

    private static final int SPAN_COUNT = 2;

    private Picasso mImageDownloader;

    private RecyclerView mRecyclerView;

    private ImagesAdapter mAdapter;

    private Image mImageDetailsImageModel;

    private final Bus mBus = EventBusCreator.defaultEventBus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.images_list);

        initializeImagesDownloader();

        initializeAdapter();

        initializeImages();

        initializeRecyclerView();

        initializeTitle();

        initializeSavedImageModel(savedInstanceState);

        initializeSwitchButton();
    }

    private void initializeImagesDownloader() {
        mImageDownloader = Picasso.with(this);
    }

    private void initializeAdapter() {
        mAdapter = new ImagesAdapter(this, mImagesList, mImageDownloader, SPAN_COUNT);
    }

    private void initializeImages() {
        // load images
        getLoaderManager().initLoader(0, null, new ImageFilesCreateLoader(this, new ImageFilesCreateLoader.LoadFinishedCallback() {
            @Override
            public void onLoadFinished(List<Image> imagesList) {
                mImagesList.addAll(imagesList);
                mAdapter.notifyDataSetChanged();
            }
        })).forceLoad();
    }

    private void initializeSwitchButton() {
        Button switchButton = (Button) findViewById(R.id.switch_to);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            switchButton.setVisibility(View.VISIBLE);
            switchButton.setText("Switch to Lollipop List Activity");
            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ImagesListActivity.this.finish();

                    Intent startActivity_v21_intent = new Intent(ImagesListActivity.this, ImagesListActivity_v21.class);
                    startActivity(startActivity_v21_intent);

                }
            });
        }
    }

    private void initializeSavedImageModel(Bundle savedInstanceState) {
        Log.v(TAG, "initializeSavedImageModel, savedInstanceState " + savedInstanceState);

        if(savedInstanceState != null){
            mImageDetailsImageModel = savedInstanceState.getParcelable(IMAGE_DETAILS_IMAGE_MODEL);
        }

        if(mImageDetailsImageModel != null) {
            updateModel(mImageDetailsImageModel);
        }
    }

    private void initializeTitle() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("List Activity Ice Cream Sandwich");
    }

    private void initializeRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.accounts_recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.setAdapter(mAdapter);
        // we don't need animation when items are hidden or shown
        mRecyclerView.setItemAnimator(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        mBus.register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        mBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        if(mRecyclerView.getChildCount() > 1){
            Log.v(TAG, "onResume, " + mRecyclerView.getChildAt(4).getVisibility());
        }
    }

    /**
     * This method is called when event is sent from {@link ImageDetailsActivity}
     * via event bus.
     *
     * When it's called it means that transition animation started and we have to hide the image on this activity in order to look
     * like image from here is transitioned to the new screen
     *
     */
    @Subscribe
    public void hideImageThumbnail(ChangeImageThumbnailVisibility message){
        Log.v(TAG, ">> hideImageThumbnail");

        mImageDetailsImageModel.setVisibility(message.isVisible());

        updateModel(mImageDetailsImageModel);

        Log.v(TAG, "<< hideImageThumbnail");
    }

    /**
     * This method basically changes visibility of concrete item
     */
    private void updateModel(Image imageToUpdate) {
        Log.v(TAG, "updateModel, imageToUpdate " + imageToUpdate);
        for (Image image : mImagesList) {

            if(image.equals(imageToUpdate)){
                Log.v(TAG, "updateModel, found imageToUpdate " + imageToUpdate);
                image.setVisibility(imageToUpdate.isVisible());
                break;
            }
        }
        int index = mImagesList.indexOf(imageToUpdate);
        Log.v(TAG, "updateModel, index " + index);

        mAdapter.notifyItemChanged(index);

        /**
         * For some reason recycler view is not always redrawn when adapter updated.
         * onBindViewHolder is called but image doesn't disappear from screen
         * That's why we have to do this invalidation
         */
        Rect dirty = new Rect();
        mRecyclerView.getChildAt(index).getDrawingRect(dirty);
        mRecyclerView.invalidate(dirty);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(IMAGE_DETAILS_IMAGE_MODEL, mImageDetailsImageModel);
    }

    @Override
    public void enterImageDetails(String sharedImageTransitionName, File imageFile, final ImageView image, Image imageModel) {
        Log.v(TAG, "enterImageDetails, imageFile " + imageFile);
        Log.v(TAG, "enterImageDetails, image.getScaleType() " + image.getScaleType());

        /**
         * We store this model for two purposes:
         * 1. When image on the next screen will be transitioned we have to hide the view
         * representation of this model in order it to look like this exact view is transitioned.
         * Transitioned view will leave empty space behind it
         *
         * 2. Activity might be destroyed at some point and we have to store this information to restore view
         * visibility when activity is recreated.
         */
        mImageDetailsImageModel = imageModel;

        int[] screenLocation = new int[2];
        image.getLocationInWindow(screenLocation);

        Intent startIntent = ImageDetailsActivity.getStartIntent(this, imageFile,
                screenLocation[0],
                screenLocation[1],
                image.getWidth(),
                image.getHeight(),
                image.getScaleType());

        startActivity(startIntent);
    }

}
