package dev.m.hussein.placestask.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.m.hussein.placestask.R;

/**
 * Created by Dev. M. Hussein on 9/17/2017.
 */

public class DrawableConfig {



    /**
     * use this to create bitmap with waterMark
     *
     * @param source source bitmap
     * @param waterMark waterMark Bitmap which will be added on source bitmap
     * */
    public static Bitmap addWaterMark(Bitmap source , Bitmap waterMark){
        if (source == null) return null;
        int w = source.getWidth();
        int h = source.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, source.getConfig());

        waterMark = Bitmap.createScaledBitmap(waterMark , 100 , 100 , false);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(source, 0, 0, null);

        Paint paint = new Paint();
        paint.setAlpha(80);
        canvas.drawBitmap(waterMark , (w/2) - (waterMark.getWidth()/2)
                , (h/2) - (waterMark.getHeight()/2) , paint);
        return result;
    }


    /**
     * using this to get bitmap from resources drawable
     *
     * @param drawableRes drawable resource id
     * */
    public static Bitmap getBitmapFromResources(Context context , int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(context , drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    /**
     * using this to save image
     *
     * @param bitmap bitmap which will be save
     * @param name this name of file
     * */
    public  static File saveBitmapAsFile(Context context, Bitmap bitmap , String name) {

        if (bitmap == null) return null;
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = null;
        try {
            file = new File(directory , name+".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(context , "Image Downloaded Successfully" , Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("SHARE_TAG"  , "error : "+e.toString());
            Toast.makeText(context , "Error While Downloading Image" , Toast.LENGTH_LONG).show();
        }

        return file;
    }


}
