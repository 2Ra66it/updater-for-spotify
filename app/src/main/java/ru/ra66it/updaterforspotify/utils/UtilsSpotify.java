package ru.ra66it.updaterforspotify.utils;

import android.content.pm.PackageManager;
import android.util.Log;

import ru.ra66it.updaterforspotify.MyApplication;

/**
 * Created by 2Rabbit on 20.09.2017.
 */
public class UtilsSpotify {

    private static final String TAG = UtilsSpotify.class.getSimpleName();

    public static Boolean isSpotifyInstalled() {
        boolean res;

        try {
            MyApplication.getContext().getPackageManager().getPackageInfo("com.spotify.music", 0);
            res = true;
        } catch (PackageManager.NameNotFoundException e) {
            res = false;
        }

        return res;
    }

    public static String getInstalledSpotifyVersion() {
        String version = "";

        try {
            version = MyApplication.getContext().getPackageManager().getPackageInfo("com.spotify.music", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Spotify not installed");
        }

        return version;
    }

    public static boolean isSpotifyUpdateAvailable(String installedVersion, String latestVersion) {
        if (installedVersion.isEmpty()) return true;
        final int installVers = Integer.parseInt(installedVersion.replaceAll("[^0-9]", ""));
        final int latestVers = Integer.parseInt(latestVersion.replaceAll("[.\\s]", ""));
        return installVers < latestVers;
    }
}
