package ru.ra66it.updaterforspotify;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by 2Rabbit on 24.09.2017.
 */

public class QueryPreferneces {

    private static final String PREF_LAST_VERSION = "latest_version";
    private static final String PREF_LAST_VERSION_NAME = "latest_version_name";
    private static final String PREF_LATEST_LINK = "latest_link";
    private static final String PREF_NOTIFICATION = "notification";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";


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

    public static boolean getNotification(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_NOTIFICATION, true);
    }

    public static void setNotification(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_NOTIFICATION, isOn)
                .apply();
    }


    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }

    public static String getLatestVersionName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_VERSION_NAME, null);
    }

    public static void setLatestVersionName(Context context, String latestVersion) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_VERSION_NAME, latestVersion)
                .apply();
    }

    public static String getLatestLink(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LATEST_LINK, null);
    }

    public static void setLatestLink(Context context, String latestVersion) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LATEST_LINK, latestVersion)
                .apply();
    }


}
