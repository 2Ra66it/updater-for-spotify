package ru.ra66it.updaterforspotify;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by 2Rabbit on 24.09.2017.
 */

public class QueryPreferneces {

    private static final String PREF_NOTIFICATION = "notification";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";



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

}
