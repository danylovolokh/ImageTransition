package com.volokh.danylo.imagetransition;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class ImagesViewHolder extends RecyclerView.ViewHolder{

    public final ImageView image;

    public ImagesViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
    }
}
