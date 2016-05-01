package com.dbuggrz.helpers;

/**
 * Created by jasonsit on 4/30/16.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;


public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public boolean scaleImage;

    public Activity parentActivity;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Log.e("DownloadImageTask", "Downloading image from: " + urldisplay);
        Bitmap mIcon11 = null;
        try {
            InputStream in = new  java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }


//    private float getBitmapScalingFactor(Bitmap bm) {
//        // Get display width from device
//        int displayWidth = parentActivity.getWindowManager().getDefaultDisplay().getWidth();
//
//        // Get margin to use it for calculating to max width of the ImageView
//        LinearLayout.LayoutParams layoutParams =
//                (LinearLayout.LayoutParams)this.imageView.getLayoutParams();
//        int leftMargin = layoutParams.leftMargin;
//        int rightMargin = layoutParams.rightMargin;
//
//        // Calculate the max width of the imageView
//        int imageViewWidth = displayWidth - (leftMargin + rightMargin);
//
//        // Calculate scaling factor and return it
//        return ( (float) imageViewWidth / (float) bm.getWidth() );
//    }
}
