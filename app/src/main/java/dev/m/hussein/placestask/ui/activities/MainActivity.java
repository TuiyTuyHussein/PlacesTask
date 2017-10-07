package dev.m.hussein.placestask.ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;

import org.afinal.simplecache.ACache;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.m.hussein.placestask.R;
import dev.m.hussein.placestask.api.Api;
import dev.m.hussein.placestask.config.Config;
import dev.m.hussein.placestask.models.Item;
import dev.m.hussein.placestask.ui.adapter.ExploreAdapter;
import dev.m.hussein.placestask.ui.adapter.FeaturedAdapter;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements Api.OnItemsLoaded {
    @Bind(R.id.featuredList)
    RecyclerView featuredRecyclerView;
    @Bind(R.id.featuredProgress)
    ProgressBar featuredProgress;
    @Bind(R.id.exploreList)
    RecyclerView exploreRecyclerView;
    @Bind(R.id.exploreProgress)
    ProgressBar exploreProgress;
    private boolean loading = false;

    private Context context;
    private List<Item> featuredArray =new LinkedList<>()
            , exploreArray = new LinkedList<>();

    private EndlessRecyclerViewAdapter exploreAdapter;
    private FeaturedAdapter featuredAdapter;
    ACache mCache ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        mCache = ACache.get(this);

        setupFeaturedViews();
        setupExploreViews();

    }

    private void setupExploreViews() {
        StaggeredGridLayoutManager exploreGridLayoutManager = new StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL);
        exploreRecyclerView.setLayoutManager(exploreGridLayoutManager);
        ExploreAdapter adapter = new ExploreAdapter(context , exploreArray);
        exploreAdapter = new EndlessRecyclerViewAdapter(context, adapter, new EndlessRecyclerViewAdapter.RequestToLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getExploreItems();
            }
        } , R.layout.loading_view , true);
        exploreRecyclerView.setAdapter(exploreAdapter);
        exploreAdapter.onDataReady(false);
        getExploreItems();


    }

    private Call call;
    private int lastId = 0;
    private void getExploreItems() {
        if (exploreArray.size() == 0 && !Config.isNetworkAvailable(this)){
            Log.i("JSON_DATA_TAG" , "cache explore : "+mCache.getAsString("explore"));
            Gson gson = new GsonBuilder().create();
            List<Item> exploreArray = Arrays.asList(gson.fromJson(mCache.getAsString("explore") , Item[].class));
            if (exploreArray == null) {
                Toast.makeText(this, "Object cache is null ...", Toast.LENGTH_SHORT)
                        .show();
            }
            handleExploreResponse(exploreArray);
            return;
        }
        if (call == null)
            call = Api.connection.loadItems(Api.METHODS.EXPLORE , 10 , lastId , this);
    }

    private void setupFeaturedViews() {
        LinearLayoutManager featuredLayoutManager = new LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false);
        featuredRecyclerView.setLayoutManager(featuredLayoutManager);
        featuredAdapter = new FeaturedAdapter(context , featuredArray);
        featuredRecyclerView.setAdapter(featuredAdapter);
        getFeaturedItems();

    }

    private void getFeaturedItems() {
        if (featuredArray.size() == 0 && !Config.isNetworkAvailable(this)){
            Log.i("JSON_DATA_TAG" , "cache featured : "+mCache.getAsString("featured"));
            Gson gson = new GsonBuilder().create();
            List<Item> featuredArray = Arrays.asList(gson.fromJson(mCache.getAsString("featured") , Item[].class));
            handleFeaturedResponse(featuredArray);
            return;
        }
        Api.connection.loadItems(Api.METHODS.FEATURED , 0 , 0 , this);
    }




    @Override
    public void onResponse(Api.METHODS methods, String response) {
        Log.i("STREAM_TAG" , "response : "+ response);
        List<Item> items ;
        Gson gson = new GsonBuilder().create();
        items = Arrays.asList(gson.fromJson(response , Item[].class));
        if (methods == Api.METHODS.FEATURED){
            handleFeaturedResponse(items);
        }else if (methods == Api.METHODS.EXPLORE){
            handleExploreResponse(items);
        }
    }


    @Override
    public void onResponseFailure(Api.METHODS methods, Exception failure) {

        Log.i("STREAM_TAG" , "failure : "+methods.name()+"  ,  "+ failure.getMessage());

    }


    private void handleFeaturedResponse(List<Item> items) {

        if (items == null || items.size() == 0) return;
        featuredArray.addAll(items);
        featuredAdapter.notifyDataSetChanged();
        if (featuredArray.size() > 0) featuredProgress.setVisibility(View.GONE);

        save();
    }



    private int lastPosition = 0;
    private void handleExploreResponse(List<Item> items) {

        if (items == null || items.size() == 0) return;
        call = null;

        int lastId = items.get(items.size() - 1 ).getId() == null ? 0 : items.get(items.size() - 1 ).getId();





        if (lastId != this.lastId){
            this.lastId = lastId;
        }else{
            this.lastId = -1;
        }


        if (exploreAdapter != null && !exploreRecyclerView.isComputingLayout())
            exploreAdapter.onDataReady(lastId != -1);

        exploreArray.addAll(items);
        exploreAdapter.notifyItemRangeInserted(lastPosition , items.size());
        lastPosition = exploreArray.size() - 1;
        if (exploreArray.size() > 0) exploreProgress.setVisibility(View.GONE);

        save();
    }



    private void save(){
        Gson gson = new GsonBuilder().create();
        if (featuredArray != null && featuredArray.size() > 0){
            JsonArray featuredJsonArray = gson.toJsonTree(featuredArray).getAsJsonArray();
            Log.i("JSON_DATA_TAG" , "featured : "+featuredJsonArray.toString());
            mCache.put("featured" , featuredJsonArray.toString() , ACache.TIME_DAY);
        }

        if (exploreArray != null && exploreArray.size() > 0) {
            JsonArray exploreJsonArray = gson.toJsonTree(exploreArray).getAsJsonArray();
            Log.i("JSON_DATA_TAG", "explore : " + exploreJsonArray.toString());
            mCache.put("explore", exploreJsonArray.toString(), ACache.TIME_DAY);
        }
    }


}
