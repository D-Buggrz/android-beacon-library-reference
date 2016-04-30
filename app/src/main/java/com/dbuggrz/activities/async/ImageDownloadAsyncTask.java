package com.dbuggrz.activities.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Mike on 4/30/2016.
 */
public class ImageDownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private static final String LOG_TAG = ImageDownloadAsyncTask.class.getName();

    private AsyncImageCallback callback;

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            Log.d(LOG_TAG, "Downloading image: " + params[0]);
            URL url = new URL(params[0]);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        super.onPostExecute(image);
        this.callback.downloadedBitmap(image);
    }
}
