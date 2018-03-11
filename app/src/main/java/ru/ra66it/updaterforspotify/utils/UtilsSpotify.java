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
        Boolean res;

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


    public static Boolean isSpotifyUpdateAvailable(String installedVersion, String latestVersion) {
        String install = installedVersion;
        int installVers = 0;

        if (installedVersion.isEmpty())      {
            install = "0";
        } else {
            installVers = Integer.parseInt(install.replaceAll("[^0-9]", ""));
        }

        int latestVers = Integer.parseInt(latestVersion.replaceAll("[.]", ""));

        return installVers < latestVers;
    }
}
