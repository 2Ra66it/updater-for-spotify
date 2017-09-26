package ru.ra66it.updaterforspotify;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by 2Rabbit on 24.09.2017.
 */

public class QueryPreferneces {

    private static final String PREF_LAST_VERSION = "latest_version";
    private static final String PREF_NOTIFICATION = "notification";

    public static String getLatestVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_VERSION, null);
    }

    public static void setLatestVersion(Context context, String latestVersion) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_VERSION, latestVersion)
                .apply();
    }

    public static void setNotification(Context context, boolean setNotif) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_NOTIFICATION, setNotif)
                .apply();
    }

    public static boolean getNotification(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_NOTIFICATION, true);
    }
}
