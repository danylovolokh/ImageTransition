package com.volokh.danylo.imagetransition;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.volokh.danylo.imagetransition.adapter.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by danylo.volokh on 3/12/16.
 */
public class ImageFilesCreateLoader implements LoaderManager.LoaderCallbacks<List<File>> {

    private static final String TAG = ImageFilesCreateLoader.class.getSimpleName();

    private static final String AVATAR = "avatar.png";
    private static final String BEST_APP_OF_THE_YEAR = "best_app_of_the_year.png";
    private static final String HB_DANYLO = "hb_danylo.png";
    private static final String LENA_DANYLO_VIKTOR = "lena_danylo_victor.png";
    private static final String VENECIA_LENA_DANYLO_OLYA = "venecia_lena_danylo_olya.png";
    private static final String DANYLO = "danylo.jng";

    private static final SparseArray<String> mImagesResourcesList = new SparseArray<>();

    static {
        mImagesResourcesList.put(R.raw.avatar, AVATAR);
        mImagesResourcesList.put(R.raw.best_app_of_the_year, BEST_APP_OF_THE_YEAR);
        mImagesResourcesList.put(R.raw.hb_danylo, HB_DANYLO);
        mImagesResourcesList.put(R.raw.lena_danylo_victor, LENA_DANYLO_VIKTOR);
        mImagesResourcesList.put(R.raw.venecia_lena_danylo_olya, VENECIA_LENA_DANYLO_OLYA);
        mImagesResourcesList.put(R.raw.danylo, DANYLO);
    }

    private final Context mContext;
    private LoadFinishedCallback mLoadFinishedCallback;

    public interface LoadFinishedCallback{
        void onLoadFinished(List<Image> imagesList);
    }

    public ImageFilesCreateLoader(Context context, LoadFinishedCallback loadFinishedCallback) {
        mContext = context;
        mLoadFinishedCallback = loadFinishedCallback;
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<File>>(mContext) {
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
        File fileDir = mContext.getCacheDir();

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
            InputStream inputStream = mContext.getResources().openRawResource(resource);
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, len);
            }

            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e1) {
        }
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        Log.v(TAG, "onLoadFinished, data " + data);

        fillImageList(data);
    }

    private void fillImageList(List<File> data) {
        List<Image> imagesList = new ArrayList<>();

        int imageId = 0;
        int times = 7;
        while(--times > 0){
            for (int i = 0; i < data.size(); i++, imageId++) {
                File file = data.get(i);

                Log.v(TAG, "fillImageList, imageId " + imageId);

                imagesList.add(new Image(imageId, file));

            }
        }
        mLoadFinishedCallback.onLoadFinished(imagesList);

    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {

    }
}
