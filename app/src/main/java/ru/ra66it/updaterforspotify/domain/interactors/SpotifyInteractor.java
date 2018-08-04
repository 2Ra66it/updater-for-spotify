package ru.ra66it.updaterforspotify.domain.interactors;

import javax.inject.Inject;

import io.reactivex.Observable;
import ru.ra66it.updaterforspotify.domain.models.Spotify;
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository;

public class SpotifyInteractor {

    private SpotifyRepository spotifyRepository;

    @Inject
    public SpotifyInteractor(SpotifyRepository spotifyRepository) {
        this.spotifyRepository = spotifyRepository;
    }

    public Observable<Spotify> getLatestSpotify() {
        return spotifyRepository.getLatestSpotify();
    }
}
