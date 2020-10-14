package com.lucas.omnia.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoadAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private URL url;
    private ImageView imageView;
    private boolean circular;

    public ImageLoadAsyncTask(URL url, ImageView imageView, boolean circular) {
        this.url = url;
        this.imageView = imageView;
        this.circular = circular;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (circular) bitmap = createCircularBitmap(bitmap);

        imageView.setImageBitmap(bitmap);
    }

    private Bitmap createCircularBitmap(Bitmap result) {
        Bitmap circleBitmap = Bitmap.createBitmap(result.getWidth(), result.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(result,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(result.getWidth()/2, result.getHeight()/2, result.getWidth()/2, paint);

        return circleBitmap;
    }
}
