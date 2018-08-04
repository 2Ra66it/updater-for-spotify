package ru.ra66it.updaterforspotify;

import android.app.Application;
import android.content.Context;

import ru.ra66it.updaterforspotify.di.ApplicationComponent;
import ru.ra66it.updaterforspotify.di.ApplicationModule;
import ru.ra66it.updaterforspotify.di.DaggerApplicationComponent;
import ru.ra66it.updaterforspotify.di.NetworkModule;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

public class UpdaterApp extends Application {

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
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static Context getContext() {
        return context;
    }
}
