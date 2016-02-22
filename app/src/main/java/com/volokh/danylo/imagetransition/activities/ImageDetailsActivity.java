package com.volokh.danylo.imagetransition.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.volokh.danylo.imagetransition.R;

/**
 * Created by danylo.volokh on 2/21/2016.
 */
public class ImageDetailsActivity extends Activity{

    private TextView mImageDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_accout_activity_layout);
        mImageDescription = (TextView) findViewById(R.id.image_description);
    }
}
