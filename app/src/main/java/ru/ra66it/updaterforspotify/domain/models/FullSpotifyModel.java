package ru.ra66it.updaterforspotify.domain.models;

/**
 * Created by 2Rabbit on 14.01.2018.
 */

public class FullSpotifyModel {

    private String latestLink;
    private String latestVersionName;
    private String latestVersionNumber;

    public FullSpotifyModel(Spotify spotify) {
        latestLink = spotify.getData().getFile().getPath();
        latestVersionName = spotify.getData().getName() + " " + spotify.getData().getFile().getVername();
        latestVersionNumber = spotify.getData().getFile().getVername();
    }

    public String getLatestLink() {
        return latestLink;
    }

    public String getLatestVersionName() {
        return latestVersionName;
    }

    public String getLatestVersionNumber() {
        return latestVersionNumber;
    }
}
