package ru.ra66it.updaterforspotify.domain.repositories;

import io.reactivex.Observable;
import ru.ra66it.updaterforspotify.domain.models.Spotify;

public interface SpotifyRepository {

    Observable<Spotify> getLatestSpotify();
}
