package ru.ra66it.updaterforspotify.storage;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by 2Rabbit on 24.09.2017.
 */

public class QueryPreferences {

    private SharedPreferences sharedPreferences;
    private static final String PREF_NOTIFICATION_ORIGIN = "notification_origin";
    private static final String PREF_IS_FIRST = "if_first";

    @Inject
    public QueryPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean getNotifications() {
        return sharedPreferences.getBoolean(PREF_NOTIFICATION_ORIGIN, false);
    }

    public void setNotifications(boolean isOn) {
        sharedPreferences.edit().putBoolean(PREF_NOTIFICATION_ORIGIN, isOn)
                .apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(PREF_IS_FIRST, true);
    }

    public void setFirstLaunch(boolean isOn) {
        sharedPreferences.edit().putBoolean(PREF_IS_FIRST, isOn).apply();
    }

}
