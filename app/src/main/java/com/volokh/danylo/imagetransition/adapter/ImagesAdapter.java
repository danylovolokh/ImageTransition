package com.volokh.danylo.imagetransition.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.R;

import java.io.File;
import java.util.List;

/**
 * Created by danylo.volokh on 2/19/2016.
 *
 * This adapter class is for the list of images. There is 6 types of item types.
 * Each on for different {@link android.widget.ImageView.ScaleType}
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

        ImagesViewHolder viewHolder = new ImagesViewHolder(item);

        /**
         * Depends on View type we set the according {@link android.widget.ImageView.ScaleType
         */

        if(viewType == 0) {
            viewHolder.image.setScaleType(ImageView.ScaleType.CENTER);
        }

        if(viewType == 1) {
            viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if(viewType == 2) {
            viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        if(viewType == 3) {
            viewHolder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        if(viewType == 4) {
            viewHolder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        if(viewType == 5) {
            viewHolder.image.setScaleType(ImageView.ScaleType.MATRIX);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ImagesViewHolder holder, final int position) {
        final Image image = mImagesList.get(position);
        Log.v(TAG, "onBindViewHolder holder.imageFile " + image.imageFile);

        Log.v(TAG, "onBindViewHolder holder.image " + holder.image);
        Log.v(TAG, "onBindViewHolder holder.matrix " + holder.image.getMatrix());
        Log.v(TAG, "onBindViewHolder holder.scaleType " + holder.image.getScaleType());

        // set transition name for sdk 21 shared element transition
        final String sharedImageTransitionName = createSharedImageTransitionName(position);
        ViewCompat.setTransitionName(holder.image, sharedImageTransitionName);

        Log.v(TAG, "onBindViewHolder isVisible " + image.isVisible());
        Log.v(TAG, "onBindViewHolder isVisible " + holder.image.getVisibility());

        if(image.isVisible()){
            mImageDownloader.load(image.imageFile).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        } else {
            /** in purpose of animation image might become invisible */
            holder.image.setVisibility(View.INVISIBLE);
        }
        Log.v(TAG, "onBindViewHolder isVisible " + holder.image.getVisibility());


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick image " + holder.image );
                Log.v(TAG, "onClick matrix " + holder.image.getMatrix() );

                mImagesAdapterCallback.enterImageDetails(sharedImageTransitionName, image.imageFile, holder.image, image);

            }
        });
        Log.v(TAG, "onBindViewHolder position " + position);
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    /**
     * We create 6 item types for each {@link android.widget.ImageView.ScaleType} there is,
     * besides:
     *      {@link android.widget.ImageView.ScaleType#FIT_START}
     *      {@link android.widget.ImageView.ScaleType#FIT_END}
     */
    @Override
    public int getItemViewType(int position) {

        if(position % 6 == 0) {
            return 0;
        }

        if(position % 5 == 0) {
            return 1;
        }

        if(position % 4 == 0) {
            return 2;
        }

        if(position % 3 == 0) {
            return 3;
        }

        if(position % 2 == 0) {
            return 4;
        }

        return 5;
    }
}
