package com.volokh.danylo.imagetransition.activities_v21;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.ImageFilesCreateLoader;
import com.volokh.danylo.imagetransition.ImagesAdapter;
import com.volokh.danylo.imagetransition.R;
import com.volokh.danylo.imagetransition.activities.ImagesListActivity;
import com.volokh.danylo.imagetransition.models.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesListActivity_v21 extends Activity implements ImagesAdapter.ImagesAdapterCallback {

    private static final String TAG = ImagesListActivity_v21.class.getSimpleName();

    private final List<Image> mImagesList = new ArrayList<>();

    private static final int SPAN_COUNT = 4;

    private Picasso mImageDownloader;
    private RecyclerView mRecyclerView;
    private ImagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images_list);

        mImageDownloader = Picasso.with(this);

        mAdapter = new ImagesAdapter(this, mImagesList, mImageDownloader, SPAN_COUNT);

        getLoaderManager().initLoader(0, null, new ImageFilesCreateLoader(this, new ImageFilesCreateLoader.LoadFinishedCallback() {
            @Override
            public void onLoadFinished(List<Image> imagesList) {
                mImagesList.addAll(imagesList);
                mAdapter.notifyDataSetChanged();
            }
        })).forceLoad();

        mRecyclerView = (RecyclerView) findViewById(R.id.accounts_recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.setAdapter(mAdapter);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText("List Activity Lollipop");

        Button switchButton = (Button) findViewById(R.id.switch_to);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            switchButton.setVisibility(View.VISIBLE);
            switchButton.setText("Switch to Ice Cream Sandwich List Activity");
            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ImagesListActivity_v21.this.finish();

                    Intent startActivityIntent = new Intent(ImagesListActivity_v21.this, ImagesListActivity.class);
                    startActivity(startActivityIntent);

                }
            });
        }
    }




    @Override
    public void enterImageDetails(String sharedImageTransitionName, File imageFile, ImageView image) {
        ActivityOptions activityOptions =
                ActivityOptions.makeSceneTransitionAnimation(this, image, sharedImageTransitionName);

        Intent startIntent = ImageDetailsActivity_v21.getStartIntent(this, sharedImageTransitionName, imageFile);
        startActivity(startIntent, activityOptions.toBundle());
    }
}
