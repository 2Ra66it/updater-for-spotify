package ru.ra66it.updaterforspotify.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import ru.ra66it.updaterforspotify.BuildConfig;

/**
 * Created by 2Rabbit on 20.09.2017.
 */

public class UtilsSpotify {

    public static String getInstalledSpotifyVersion(Context context) {
        String version = "";

        try {
            version = context.getPackageManager().getPackageInfo("com.spotify.music", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

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



    public static Boolean isUpdateAvailable(String installedVersion, String latestVersion) {
        int installVers;

        if (!installedVersion.equals("")) {
            installVers = Integer.parseInt(installedVersion.replaceAll("[a-z]", "").replaceAll("[.]", "").replaceAll("[-]", ""));
        } else {
            return true;
        }

        int latestVers = Integer.parseInt(latestVersion.replaceAll("[.]", ""));

        if (installVers == latestVers) {
            return false;
        } else {
            return  true;
        }

    }
}
