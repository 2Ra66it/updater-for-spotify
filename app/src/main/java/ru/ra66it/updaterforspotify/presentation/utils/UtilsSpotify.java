package ru.ra66it.updaterforspotify.presentation.utils;

import android.content.pm.PackageManager;
import android.util.Log;

import ru.ra66it.updaterforspotify.UpdaterApp;

/**
 * Created by 2Rabbit on 20.09.2017.
 */

public class UtilsSpotify {

    private static final String TAG = UtilsSpotify.class.getSimpleName();

    public static Boolean isSpotifyInstalled() {
        boolean res;

        try {
            UpdaterApp.getContext().getPackageManager().getPackageInfo("com.spotify.music", 0);
            res = true;
        } catch (PackageManager.NameNotFoundException e) {
            res = false;
        }

        return res;
    }

    public static String getInstalledSpotifyVersion() {
        String version = "0";

        try {
            version = UpdaterApp.getContext().getPackageManager().getPackageInfo("com.spotify.music", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Spotify not installed");
        }

        return version;
    }


    public static Boolean isSpotifyUpdateAvailable(String installedVersion, String latestedVersion) {
        int installVersion = Integer.parseInt(installedVersion.replaceAll("[^0-9]", "").replaceAll("\\s+", ""));
        int latestVersion = Integer.parseInt(latestedVersion.replaceAll("[.]", "").replaceAll("\\s+", ""));
        return installVersion < latestVersion;
    }
}
