package dev.m.hussein.placestask.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Dev. M. Hussein on 10/7/2017.
 * used this class to download bitmap from url using Picasso Target
 */

public class BitmapTarget implements Target {
    private String url;
    private Context context;
    private OnBitmapLoaded onBitmapLoaded;

    public BitmapTarget(String url , Context context , OnBitmapLoaded onBitmapLoaded) {
        this.url = url;
        this.context = context;

        this.onBitmapLoaded = onBitmapLoaded;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        /**
         * when bitmap loaded this bitmap will cached using it's url as tag
         * */
        if (onBitmapLoaded != null) onBitmapLoaded.setOnBitmapLoaded(bitmap);
        new CacheConfig(context).cacheBitmap(bitmap , url);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }


    public interface OnBitmapLoaded{
        void setOnBitmapLoaded(Bitmap onBitmapLoaded);
    }
}