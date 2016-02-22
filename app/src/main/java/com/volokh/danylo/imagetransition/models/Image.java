package com.volokh.danylo.imagetransition.models;

import android.widget.ImageView;

import java.io.File;

/**
 * Created by danylo.volokh on 2/21/2016.
 */
public class Image {

    public final File imageFile;
    public final ImageView.ScaleType scaleType;

    public Image(File imageFile, ImageView.ScaleType imageScaleType) {
        this.imageFile = imageFile;
        this.scaleType = imageScaleType;
    }
}
