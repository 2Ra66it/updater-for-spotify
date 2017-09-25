package ru.ra66it.updaterforspotify;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by 2Rabbit on 24.09.2017.
 */

public class QueryPreferneces {

    private static final String PREF_LAST_VERSION = "latest_version";

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
}
