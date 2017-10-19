package ru.ra66it.updaterforspotify;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by 2Rabbit on 24.09.2017.
 */

public class QueryPreferneces {

    private static final String PREF_NOTIFICATION_DF = "notification_df";
    private static final String PREF_NOTIFICATION_ORIGIN = "notification_origin";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";
    private static final String PREF_IS_BETA = "isBeta";
    private static final String PREF_IS_FIRST_LAUNCH = "isFirstLaunch";



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

    public static boolean isFirstLaunch(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_FIRST_LAUNCH, true);
    }

    public static void setFirstLaunch(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_FIRST_LAUNCH, isOn)
                .apply();
    }

    public static boolean isSpotifyBeta(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_BETA, true);
    }

    public static void setSpotifyBeta(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_BETA, isOn)
                .apply();
    }

}
