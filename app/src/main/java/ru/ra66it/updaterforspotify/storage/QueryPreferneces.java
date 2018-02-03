package ru.ra66it.updaterforspotify.storage;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by 2Rabbit on 24.09.2017.
 */

public class QueryPreferneces {

    private static final String PREF_NOTIFICATION_DF = "notification_df";
    private static final String PREF_NOTIFICATION_ORIGIN = "notification_origin";
    private static final String PREF_IS_ALARM_ON = "is_alarm_on";
    private static final String PREF_IS_BETA = "is_beta";
    private static final String PREF_IS_FIRST = "if_first";
    private static final String PREF_SECRET = "is_secret";


    public static boolean getNotificationDogFood(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_NOTIFICATION_DF, false);
    }

    public static void setNotificationDogFood(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_NOTIFICATION_DF, isOn)
                .apply();
    }

    public static boolean getNotificationOrigin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_NOTIFICATION_ORIGIN, false);
    }

    public static void setNotificationOrigin(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_NOTIFICATION_ORIGIN, isOn)
                .apply();
    }

    public static boolean isSpotifyBeta(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_BETA, false);
    }

    public static void setSpotifyBeta(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_BETA, isOn)
                .apply();
    }

    public static boolean getNotificationDogFoodC(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SECRET, false);
    }

    public static void setNotificationDogFoodC(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SECRET, isOn)
                .apply();
    }

    public static boolean isFirstLaunch(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_FIRST, true);
    }

    public static void setFirstLaunch(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_FIRST, isOn)
                .apply();
    }

}
