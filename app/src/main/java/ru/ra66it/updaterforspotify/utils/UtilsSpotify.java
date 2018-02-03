package ru.ra66it.updaterforspotify.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by 2Rabbit on 20.09.2017.
 */

public class UtilsSpotify {

    private static final String TAG = UtilsSpotify.class.getSimpleName();

    public static Boolean isSpotifyInstalled(Context context) {
        Boolean res;

        try {
            context.getPackageManager().getPackageInfo("com.spotify.music", 0);
            res = true;
        } catch (PackageManager.NameNotFoundException e) {
            res = false;
        }

        return res;
    }

    public static String getInstalledSpotifyVersion(Context context) {
        String version = "";

        try {
            version = context.getPackageManager().getPackageInfo("com.spotify.music", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Spotify not installed");
        }

        return version;
    }


    public static boolean isDogFoodInstalled(Context context) {

        String version = "";

        try {
            version = context.getPackageManager().getPackageInfo("com.spotify.music", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Spotify Dogfood not installed");
        }

        String dfv = version.replaceAll("[0-9]", "").replaceAll("[.]", "").replaceAll("[-]", "");

        return (dfv.equals("dogfood") || dfv.contains("dogfood"));
    }


    public static Boolean isSpotifyUpdateAvailable(String installedVersion, String latestVersion) {
        int installVers;
        String dfv;

        if (!installedVersion.equals("")) {
            dfv = installedVersion.replaceAll("[0-9]", "").replaceAll("[.]", "").replaceAll("[-]", "");
        } else {
            return true;
        }

        if (!(dfv.equals("dogfood") || !dfv.contains("dogfood"))) {
            installVers = Integer.parseInt(installedVersion.replaceAll("[^0-9]", ""));
            int latestVers = Integer.parseInt(latestVersion.replaceAll("[.]", ""));

            return installVers < latestVers;
        } else {
            return true;
        }

    }

    public static Boolean isDogfoodUpdateAvailable(String installedVersion, String latestVersion) {
        int installVers;
        String dfv;

        if (!installedVersion.equals("")) {
            dfv = installedVersion.replaceAll("[0-9]", "").replaceAll("[.]", "").replaceAll("[-]", "");
        } else {
            return true;
        }

        if (dfv.equals("dogfood") || dfv.contains("dogfood")) {
            installVers = Integer.parseInt(installedVersion.replaceAll("[^0-9]", ""));
            int latestVers = Integer.parseInt(latestVersion.replaceAll("[.]", ""));

            return installVers < latestVers;
        } else {
            return true;
        }


    }
}
