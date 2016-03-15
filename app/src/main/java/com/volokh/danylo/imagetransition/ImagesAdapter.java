package com.volokh.danylo.imagetransition;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.activities_v21.ImageDetailsActivity_v21;
import com.volokh.danylo.imagetransition.models.Image;

import java.io.File;
import java.util.List;

/**
 * Created by danylo.volokh on 2/19/2016.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

    private static final String TAG = ImagesAdapter.class.getSimpleName();

    private final ImagesAdapterCallback mImagesAdapterCallback;
    private final List<Image> mImagesList;
    private final Picasso mImageDownloader;
    private final int mSpanCount;

    public interface ImagesAdapterCallback{
        void enterImageDetails(String sharedImageTransitionName, File imageFile, ImageView image, Image imageModel);
    }

    public ImagesAdapter(ImagesAdapterCallback imagesAdapterCallback, List<Image> imagesList, Picasso imageDownloader, int spanCount) {

        mImagesAdapterCallback = imagesAdapterCallback;
        mImagesList = imagesList;
        mImageDownloader = imageDownloader;
        mSpanCount = spanCount;
    }

    public static String createSharedImageTransitionName(int position){
        return "SharedImage" + position;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
        layoutParams.height = item.getResources().getDisplayMetrics().widthPixels / mSpanCount;
        return new ImagesViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final ImagesViewHolder holder, final int position) {
        final Image image = mImagesList.get(position);
        holder.image.setScaleType(image.scaleType);

        final String sharedImageTransitionName = createSharedImageTransitionName(position);
        ViewCompat.setTransitionName(holder.image, sharedImageTransitionName);

        mImageDownloader.load(image.imageFile).into(holder.image);

        Log.v(TAG, "onBindViewHolder isVisible " + image.isVisible() );

        /** in purpose of animation image might become invisible */
        holder.image.setVisibility(image.isVisible() ? View.VISIBLE : View.INVISIBLE);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mImagesAdapterCallback.enterImageDetails(sharedImageTransitionName, image.imageFile, holder.image, image);

            }
        });
        Log.v(TAG, "onBindViewHolder position " + position);
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

}
