package dev.m.hussein.placestask.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Dev. M. Hussein on 10/7/2017.
 */

public class BitmapTarget implements Target {
    private String url;
    private Context context;

    public BitmapTarget(String url , Context context) {
        this.url = url;
        this.context = context;

    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        new CacheConfig(context).cacheBitmap(bitmap , url);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}