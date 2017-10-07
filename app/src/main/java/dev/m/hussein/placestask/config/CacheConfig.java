package dev.m.hussein.placestask.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

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

    public  void cacheBitmap (Bitmap bitmap , String tag){
        if (bitmap == null) return;
        new CacheBitmapAsync(tag , bitmap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public Bitmap getCachedBitmap (String tag){
        return aCache.getAsBitmap(tag);
    }


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
