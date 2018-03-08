package ru.ra66it.updaterforspotify;

import android.app.Application;
import android.content.Context;

import ru.ra66it.updaterforspotify.di.ApplicationComponent;
import ru.ra66it.updaterforspotify.di.DaggerApplicationComponent;
import ru.ra66it.updaterforspotify.di.RestModule;
import ru.ra66it.updaterforspotify.di.SharedPreferencesModule;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

public class MyApplication extends Application {

    private static ApplicationComponent applicationComponent;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initComponent();
    }

    private void initComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .restModule(new RestModule())
                .sharedPreferencesModule(new SharedPreferencesModule(this))
                .build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static Context getContext() {
        return context;
    }
}
