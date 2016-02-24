package com.volokh.danylo.imagetransition;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.models.Image;

import java.util.List;

/**
 * Created by danylo.volokh on 2/19/2016.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

    private static final String TAG = ImagesAdapter.class.getSimpleName();
    private final List<Image> mImagesList;
    private final Picasso mImageDownloader;
    private final int mSpanCount;

    public ImagesAdapter(List<Image> imagesList, Picasso imageDownloader, int spanCount) {

        this.mImagesList = imagesList;
        this.mImageDownloader = imageDownloader;
        mSpanCount = spanCount;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
        layoutParams.height = item.getResources().getDisplayMetrics().widthPixels / mSpanCount;
        Log.v(TAG, "onCreateViewHolder viewType " + viewType);
        return new ImagesViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        Image image = mImagesList.get(position);
        holder.image.setScaleType(image.scaleType);
        mImageDownloader.load(image.imageFile).into(holder.image);

        if(image.scaleType == ImageView.ScaleType.CENTER_CROP){
            Log.v(TAG, "onBindViewHolder setWillNotCacheDrawing " + position);


        }
        Log.v(TAG, "onBindViewHolder position " + position);
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

}
