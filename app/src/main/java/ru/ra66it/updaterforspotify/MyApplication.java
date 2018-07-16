package ru.ra66it.updaterforspotify;

import android.app.Application;
import android.content.Context;

import ru.ra66it.updaterforspotify.di.AppModule;
import ru.ra66it.updaterforspotify.di.ApplicationComponent;
import ru.ra66it.updaterforspotify.di.DaggerApplicationComponent;
import ru.ra66it.updaterforspotify.di.RestModule;
import ru.ra66it.updaterforspotify.di.SharedPreferencesModule;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

public class MyApplication extends Application {

    private ApplicationComponent applicationComponent;

    private Context context;
    private static MyApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        context = getApplicationContext();
        initComponent();
    }

    private void initComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .restModule(new RestModule())
                .sharedPreferencesModule(new SharedPreferencesModule())
                .build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return INSTANCE.applicationComponent;
    }

    public static Context getContext() {
        return INSTANCE.context;
    }
}