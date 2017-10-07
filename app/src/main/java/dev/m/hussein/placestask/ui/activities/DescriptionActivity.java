package dev.m.hussein.placestask.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.m.hussein.placestask.R;
import dev.m.hussein.placestask.config.BitmapTarget;
import dev.m.hussein.placestask.config.CacheConfig;
import dev.m.hussein.placestask.config.Config;
import dev.m.hussein.placestask.config.DrawableConfig;
import dev.m.hussein.placestask.models.Item;

public class DescriptionActivity extends AppCompatActivity {

    private static final int STORAGE_REQUEST_CODE = 12;

    /**
     * define views using butterKnife
     * */
    @Bind(R.id.image)
    ImageView imageView;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.price)
    TextView price;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.download)
    AppCompatImageButton download;



    Item item;
    private CacheConfig aCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        ButterKnife.bind(this);

            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

        aCache = new CacheConfig(this);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
        getData();
        inflateData();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });
    }




    /**
     * inflate data with returned from intent
     * */
    private void inflateData() {
        description.setText(item.getPlaceDescription());
        price.setText(String.valueOf(item.getPrice()).concat("$"));

        Log.i("IMAGEURL" , "url : "+item.getImage().getUrl());

        /**
         * check if internet providers is connected
         * if connected --> will download image
         * else --> will get image from cache
         * */
        if (Config.isNetworkAvailable(this)) {
            Picasso.with(this)
                    .load(item.getImage().getUrl())
                    .placeholder(new ColorDrawable(Color.LTGRAY))
                    .into(imageView);


            Picasso.with(this)
                    .load(item.getImage().getUrl())
                    .into(new BitmapTarget(item.getImage().getUrl() , this , null));
        }else {
            Bitmap cachedBitmap = aCache.getCachedBitmap(item.getImage().getUrl());
            imageView.setImageBitmap(cachedBitmap);
        }

    }

    /**
     * get data from intent
     * */
    private void getData() {
        item = (Item) getIntent().getSerializableExtra("item");
    }



    /**
     * download image when click on download button
     * */
    private void downloadImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check permission for marshmellow.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Camera permission has not been granted.
                // Camera permission has not been granted yet. Request it directly.
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                return ;
            }
        }


        Log.i("IMAGEURL" , "url : "+item.getImage().getUrl());


        Picasso.with(this)
                .load(item.getImage().getUrl())
                .into(new BitmapTarget(item.getImage().getUrl(), this, new BitmapTarget.OnBitmapLoaded() {
                    @Override
                    public void setOnBitmapLoaded(Bitmap onBitmapLoaded) {
                        Bitmap bitmapWithWaterMark = DrawableConfig.addWaterMark(onBitmapLoaded , DrawableConfig.getBitmapFromResources(DescriptionActivity.this , R.mipmap.ic_launcher));
                        File file = DrawableConfig.saveBitmapAsFile(DescriptionActivity.this , bitmapWithWaterMark , "Image"+item.getId());
                        openFile(file);
                    }
                }));



    }

    /**
     * when image download this will open it on device gallery
     * */
    private void openFile(File file) {
        Uri uri =  Uri.fromFile(file);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        String mime = "*/*";
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (mimeTypeMap.hasExtension(
                mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
            mime = mimeTypeMap.getMimeTypeFromExtension(
                    mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        intent.setDataAndType(uri,mime);
        try {
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            downloadImage();
        }
    }
}
