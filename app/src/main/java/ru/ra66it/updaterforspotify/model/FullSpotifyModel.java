package ru.ra66it.updaterforspotify.model;

/**
 * Created by 2Rabbit on 14.01.2018.
 */

public class FullSpotifyModel {

    private String latestLink;
    private String latestVersionName;
    private String latestVersionNumber;

    public FullSpotifyModel(Spotify spotify) {
        latestLink = spotify.getUrl();
        latestVersionName = spotify.getName();
        latestVersionNumber = spotify.getVersion();
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
