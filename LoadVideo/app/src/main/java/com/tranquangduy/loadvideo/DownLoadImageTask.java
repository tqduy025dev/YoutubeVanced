package com.tranquangduy.loadvideo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;




public class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
    HttpURLConnection connection;

    ImageView imageView;

    public DownLoadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());

            return BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
