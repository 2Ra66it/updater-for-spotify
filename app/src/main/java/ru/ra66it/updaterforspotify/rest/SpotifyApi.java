package ru.ra66it.updaterforspotify.rest;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import ru.ra66it.updaterforspotify.model.Spotify;


/**
 * Created by 2Rabbit on 22.09.2017.
 */

public interface SpotifyApi {

    String BASE_URL = "https://api.github.com/";


    @GET("repos/sergiocastell/spotify-dogfood/releases/latest")
    Call<Spotify> getLatestDogFood();

    @GET("repos/spotify-dogfood/spotify-bin/releases/latest")
    Call<Spotify> getLatestOrigin();

    @GET("repos/spotify-dogfood/spotify-beta-bin/releases/latest")
    Call<Spotify> getLatestOriginBeta();


    class Factory {
        private static SpotifyApi service;

        public static SpotifyApi getInstance() {
            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(BASE_URL)
                        .build();
                service = retrofit.create(SpotifyApi.class);
                return service;
            } else {
                return service;
            }
        }
    }
}
