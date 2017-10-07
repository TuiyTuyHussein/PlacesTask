package dev.m.hussein.placestask.api;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Dev. M. Hussein on 10/6/2017.
 */

public interface Api {

    /**
     * this class used to initialize variables
     * E.X.  links , tags
     * */
    class Constants{
        // api service links.
        public static final String BASE_SERVER_URI = "http://grapes-n-berries.getsandbox.com/sightsofegypt/";
    }


    /**
     * Enums for methods types
     * */
    enum  METHODS{
        FEATURED , EXPLORE
    }


    /**
     * To create featured places method
     * */
    @GET("featured")
    Call<ResponseBody> getFeaturedMethod();


    /**
     * To create explore places method
     *
     * @param count use this parameter to define the count of response items
     * @param from use this parameter to define the first id to get data from
     * */
    @GET("explore")
    Call<ResponseBody> getExploreMethod(@Query("count") int count , @Query("from") int from);


    /**
     * this used to start connecting to server and get data
     * */
    class connection {


        /**
         * initialize retrofit
         * */
        private static Retrofit open() {

            /**
             * to create time out request connecting to the server
             * */
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build();


            return new Retrofit.Builder()
                    .baseUrl(Constants.BASE_SERVER_URI)
                    .client(okHttpClient)
                    .build();

        }




        /**
         * using this to load places
         *
         * @param methods to define return type
         * @param count count for returned items
         * @param from start id to get from
         * @param onItemsLoaded listener to return data from this class to views
         * */
        public static Call<ResponseBody> loadItems(final METHODS methods , int count , int from , final OnItemsLoaded onItemsLoaded) {
            Retrofit retrofit = Api.connection.open();
            Api apiService = retrofit.create(Api.class);
            // create server request based on request type.

            Call<ResponseBody> call = null;
            if (methods == METHODS.FEATURED){
                call = apiService.getFeaturedMethod();
            }else if (methods == METHODS.EXPLORE) {
                call = apiService.getExploreMethod(count, from);
            }
            Log.i("STREAM_TAG" , "url : "+ call.request().url().toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.code() != 200) {
                        onItemsLoaded.onResponseFailure(methods , new Exception("server error"));
                        return;
                    }
                    try {
                        onItemsLoaded.onResponse(methods , response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        onItemsLoaded.onResponseFailure(methods , e);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                    // pass error to caller.
                    onItemsLoaded.onResponseFailure(methods , new Exception(t.getMessage()));

                }
            });
            return call;
        }



    }


    /**
     * interface used as a listener to connect presenter with views
     * */
    interface OnItemsLoaded{
        void onResponse(METHODS methods , String response);
        void onResponseFailure(METHODS methods , Exception failure);
    }

}
