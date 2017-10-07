package dev.m.hussein.placestask.ui.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.afinal.simplecache.ACache;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.m.hussein.placestask.R;
import dev.m.hussein.placestask.config.BitmapTarget;
import dev.m.hussein.placestask.config.CacheConfig;
import dev.m.hussein.placestask.config.Config;
import dev.m.hussein.placestask.models.Item;
import dev.m.hussein.placestask.ui.activities.DescriptionActivity;

/**
 * Created by Dev. M. Hussein on 10/6/2017.
 */

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.Holder> {

    private Context context;
    private List<Item> array;
    private CacheConfig aCache;
    int imageWidth;

    public ExploreAdapter(Context context, List<Item> array) {
        this.context = context;
        this.array = array;
        aCache = new CacheConfig(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int padding = Config.dpToPx(context , 22);
        width = width - padding;
        imageWidth = width / 2;
    }


    public Context getContext() {
        return context;
    }

    public List<Item> getArray() {
        return array;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_explore, parent , false);
        Holder holder = new Holder(view);
        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT));


        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Item item = getArray().get(position);
        holder.price.setText(String.valueOf(item.getPrice()).concat("$"));
        holder.description.setText(item.getPlaceDescription());



        int height = ((imageWidth-130) * item.getImage().getHeight()) / item.getImage().getWidth();

        Log.i("IMAGES_DIMEN" , "w : "+imageWidth+" , h : "+height);
        Log.i("IMAGES_DIMEN" , "nw : "+item.getImage().getWidth()+" , nh : "+item.getImage().getHeight());

        holder.image.getLayoutParams().height = height;



        if (Config.isNetworkAvailable(context)) {
            Picasso.with(getContext())
                    .load(item.getImage().getUrl())
                    .placeholder(new ColorDrawable(Color.LTGRAY))
                    .into(holder.image);


            Picasso.with(getContext())
                    .load(item.getImage().getUrl())
                    .into(new BitmapTarget(item.getImage().getUrl() , context , null));
        }else {
            Bitmap cachedBitmap = aCache.getCachedBitmap(item.getImage().getUrl());
            holder.image.setImageBitmap(cachedBitmap);
        }

        holder.itemView.setTag(R.id.position , position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We are passing Bundle to activity, these lines will animate when we laucnh activity
                Bundle bundle = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                            Pair.create( v,"imageAction") ,  Pair.create( v,"priceAction")
                    ).toBundle();
                }

                Intent intent = new Intent(context , DescriptionActivity.class);
                intent.putExtra("item" , getArray().get((int) v.getTag(R.id.position)));
                context.startActivity(intent , bundle);
            }
        });

    }



    @Override
    public int getItemCount() {
        return array == null ? 0 : array.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.description)
        TextView description;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
    }
}
