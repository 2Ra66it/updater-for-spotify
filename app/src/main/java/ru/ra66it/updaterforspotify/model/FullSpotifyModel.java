package ru.ra66it.updaterforspotify.model;

/**
 * Created by 2Rabbit on 14.01.2018.
 */

public class FullSpotifyModel {

    private String latestLink;
    private String latestVersionName;
    private String latestVersionNumber;

    public FullSpotifyModel(Fields spotify) {
        latestLink = spotify.getLink().getStringValue();
        latestVersionName = spotify.getName().getStringValue();
        latestVersionNumber = spotify.getVersion().getStringValue();
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
