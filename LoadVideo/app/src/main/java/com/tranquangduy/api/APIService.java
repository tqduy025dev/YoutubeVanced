package com.tranquangduy.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tranquangduy.model.Root;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface APIService {

    String API = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&q=the%20weeknd&key=AIzaSyD8rO5MjZrxJhqHfS2Lub5l4KNX5FhQs7o";


    Gson gson = new GsonBuilder().setDateFormat("đd-MM-yyyy HH:mm:ss").create();

    APIService api = new Retrofit.Builder().baseUrl("https://youtube.googleapis.com/youtube/")
                            .addConverterFactory(GsonConverterFactory
                            .create(gson))
                            .build()
                            .create(APIService.class);


    @GET("v3/search")
    Call<Root> searchVideo(@Query("part") String part,
                           @Query("key") String key,
                           @Query("maxResults") int maxResults,
                           @Query("type") String type,
                           @Query("q") String q);



}
