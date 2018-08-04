package ru.ra66it.updaterforspotify.data.repositories;

import javax.inject.Inject;

import io.reactivex.Observable;
import ru.ra66it.updaterforspotify.data.network.SpotifyApi;
import ru.ra66it.updaterforspotify.domain.models.Spotify;
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository;


public class SpotifyRepositoryImpl implements SpotifyRepository {

    private SpotifyApi spotifyApi;

    @Inject
    public SpotifyRepositoryImpl(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    @Override
    public Observable<Spotify> getLatestSpotify() {
        return spotifyApi.getLatestSpotify();
    }
}
