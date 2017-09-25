package ru.ra66it.updaterforspotify.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import ru.ra66it.updaterforspotify.model.Spotify;

/**
 * Created by 2Rabbit on 22.09.2017.
 */

public interface SpotifyDogfoodApi {

    String BASE_URL = "https://api.github.com/";
    String API_KEY = "repos/sergiocastell/spotify-dogfood/releases/latest";


    @GET(API_KEY)
    Call<Spotify> getLatest();


    class Factory {
        private static SpotifyDogfoodApi service;

        public static SpotifyDogfoodApi getInstance() {
            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(BASE_URL)
                        .build();
                service = retrofit.create(SpotifyDogfoodApi.class);
                return service;
            } else {
                return service;
            }
        }
    }
}
