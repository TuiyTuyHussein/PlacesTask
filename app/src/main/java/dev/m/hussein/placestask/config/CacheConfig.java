package dev.m.hussein.placestask.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.afinal.simplecache.ACache;

/**
 * Created by Dev. M. Hussein on 10/7/2017.
 */

public class CacheConfig {

    ACache aCache;
    Context context;

    public CacheConfig(Context context) {
        this.context = context;
        aCache = ACache.get(context);
    }

    /**
     * create cached bitmap
     * @param bitmap source bitmap which want to cache
     * @param tag tag name to cache bitmap with it
     * */
    public  void cacheBitmap (Bitmap bitmap , String tag){
        if (bitmap == null) return;
        new CacheBitmapAsync(tag , bitmap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * using this method to get bitmap from cache
     *
     * @param tag tag of bitmap witch cached with it
     * */
    public Bitmap getCachedBitmap (String tag){
        return aCache.getAsBitmap(tag);
    }

    /**
     * using this method to get cached data as String
     *
     * @param tag tag of object
     * */
    public String getAsString(String tag) {
        return aCache.getAsString(tag);
    }


    /**
     * using this method to put data as String to cache
     *
     * @param tag tag of object
     * @param data data that will be cached
     * @param cachedTime use this to define when this cached data will clear
     * */
    public void put( String tag, String data,int cachedTime) {
        aCache.put(tag , data , cachedTime);
    }


    /**
     * cached bitmap on background
     * */
    private class CacheBitmapAsync extends AsyncTask<Void , Void , Void>{

        private String tag;
        private Bitmap bitmap;

        public CacheBitmapAsync(String tag, Bitmap bitmap) {
            this.tag = tag;
            this.bitmap = bitmap;
        }

        @Override
        protected Void doInBackground(Void... params) {
            aCache.put(tag , bitmap);
            return null;
        }
    }


}
