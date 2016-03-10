package com.volokh.danylo.imagetransition.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.imagetransition.ImagesAdapter;
import com.volokh.danylo.imagetransition.library.OverlayViewGroup;
import com.volokh.danylo.imagetransition.R;
import com.volokh.danylo.imagetransition.library.animators.ImageTransitionAnimator;
import com.volokh.danylo.imagetransition.models.Image;
import com.volokh.danylo.imagetransition.library.handler.TransitionHandler;
import com.volokh.danylo.imagetransition.library.animators.FadeOutAnimator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImagesListActivity extends Activity implements LoaderManager.LoaderCallbacks<List<File>>,ImagesAdapter.ImagesAdapterCallback {

    private static final String TAG = ImagesListActivity.class.getSimpleName();

    private static final SparseArray<String> mImagesResourcesList = new SparseArray<>();

    private static final String AVATAR = "avatar.png";
    private static final String BEST_APP_OF_THE_YEAR = "best_app_of_the_year.png";
    private static final String HB_DANYLO = "hb_danylo.png";
    private static final String LENA_DANYLO_VIKTOR = "lena_danylo_victor.png";
    private static final String VENECIA_LENA_DANYLO_OLYA = "venecia_lena_danylo_olya.png";
    private static final String DANYLO = "danylo.jng";


    static {
        mImagesResourcesList.put(R.raw.avatar, AVATAR);
        mImagesResourcesList.put(R.raw.best_app_of_the_year, BEST_APP_OF_THE_YEAR);
        mImagesResourcesList.put(R.raw.hb_danylo, HB_DANYLO);
        mImagesResourcesList.put(R.raw.lena_danylo_victor, LENA_DANYLO_VIKTOR);
        mImagesResourcesList.put(R.raw.venecia_lena_danylo_olya, VENECIA_LENA_DANYLO_OLYA);
        mImagesResourcesList.put(R.raw.danylo, DANYLO);

    }

    private final List<Image> mImagesList = new ArrayList<>();

    private static final int SPAN_COUNT = 4;

    private Picasso mImageDownloader;

    private static View mRootLayout;
    private RecyclerView mRecyclerView;

    private ImagesAdapter mAdapter;
    private ImageView mBackground;
    private static FrameLayout sContentView;

    public static OverlayViewGroup sOverlayViewGroup;
    private static ImageView sImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accounts_list);


        mImageDownloader = Picasso.with(this);

        mAdapter = new ImagesAdapter(this, mImagesList, mImageDownloader, SPAN_COUNT);

        getLoaderManager().initLoader(0, null, this).forceLoad();
        View decorView = getWindow().getDecorView();
        sContentView = (FrameLayout) decorView.findViewById(android.R.id.content);

        mRootLayout = findViewById(R.id.root_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.accounts_recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mRecyclerView.setAdapter(mAdapter);
        mBackground = (ImageView) findViewById(R.id.background);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View decorView = getWindow().getDecorView();

                ImageView imageView = new ImageView(ImagesListActivity.this);
                imageView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                FrameLayout contentView = (FrameLayout) decorView.findViewById(android.R.id.content);

                contentView.setDrawingCacheEnabled(true);
                contentView.getDrawingCache();
//                Bitmap bitmap = Bitmap.createBitmap(contentView.getDrawingCache());
                contentView.setDrawingCacheEnabled(false); // clear drawing cache
//                Log.v(TAG, "onClick, bitmap " + bitmap);
//
//                Log.v(TAG, "onClick, contentView " + contentView);
//                imageView.setImageBitmap(bitmap);
//                contentView.addView(imageView);
//                Log.v(TAG, "onClick, imageView.getLayoutParams() " + imageView.getLayoutParams());


//                ImagesListActivity.this.finish();
//
//                Intent startActivity_v21_intent = new Intent(ImagesListActivity.this, ImagesListActivity_v21.class);
//                startActivity(startActivity_v21_intent);

            }
        });
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<File>>(this) {
            @Override
            public List<File> loadInBackground() {
                Log.v(TAG, "loadInBackground");
                List<File> resultList = new ArrayList<>();

                for (int resourceIndex = 0; resourceIndex < mImagesResourcesList.size(); resourceIndex++) {

                    writeResourceToFile(resultList, resourceIndex);
                }
                Log.v(TAG, "loadInBackground, resultList " + resultList);

                return resultList;
            }
        };
    }

    private void writeResourceToFile(List<File> resultList, int resourceIndex) {
        File fileDir = getCacheDir();

        List<String> existingFiles = Arrays.asList(fileDir.list());

        String fileName = mImagesResourcesList.valueAt(resourceIndex);
        File file = new File(fileDir + File.separator + fileName);
        if (existingFiles.contains(fileName)) {
            resultList.add(file);
        } else {
            saveIntoFile(file, mImagesResourcesList.keyAt(resourceIndex));
            resultList.add(file);
        }
    }

    private void saveIntoFile(File file, Integer resource) {
        try {
            InputStream inputStream = getResources().openRawResource(resource);
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, len);
            }

            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        Log.v(TAG, "onLoadFinished, data " + data);

        mImageDownloader.load(data.get(5)).into(mBackground);

        fillImageList(data);
    }

    private void fillImageList(List<File> data) {
        int times = 7;
        while(times-- > 0){
            for (int i = 0; i < data.size(); i++) {
                File file = data.get(i);

                if(i % 6 == 0) {
                    mImagesList.add(new Image(file, ImageView.ScaleType.CENTER));
                    continue;
                }

                if(i % 5 == 0) {
                    mImagesList.add(new Image(file, ImageView.ScaleType.CENTER_CROP));
                    continue;
                }

                if(i % 4 == 0) {
                    mImagesList.add(new Image(file, ImageView.ScaleType.CENTER_INSIDE));
                    continue;
                }

                if(i % 3 == 0) {
                    mImagesList.add(new Image(file, ImageView.ScaleType.FIT_CENTER));
                    continue;
                }

                if(i % 2 == 0) {
                    mImagesList.add(new Image(file, ImageView.ScaleType.FIT_XY));
                    continue;
                }

                mImagesList.add(new Image(file, ImageView.ScaleType.MATRIX));
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {

    }

    @Override
    public void enterImageDetails(String sharedImageTransitionName, File imageFile, ImageView image) {
        Log.v(TAG, "enterImageDetails, imageFile " + imageFile);


        TransitionHandler.instance()
                .exitTransition()
                .fromActivity(this)
                .toActivity(ImageDetailsActivity.class)
                .withExitAnimator(new FadeOutAnimator(4000))
                .addSharedElement("TransitionName", image)
                .useSharedElementAnimator(new ImageTransitionAnimator(4000));

        Intent startIntent = ImageDetailsActivity.getStartIntent(this, sharedImageTransitionName, imageFile);
        startActivity(startIntent);
    }
}
