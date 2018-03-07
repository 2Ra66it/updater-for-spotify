package ru.ra66it.updaterforspotify.rest;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

public class RestClient {

    private String BASE_URL = "https://updater-for-spotify.firebaseio.com/";

    private Retrofit retrofit;

    public RestClient() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }


    public <S> S createService(Class<S> sClass) {
       return retrofit.create(sClass);
    }
}
