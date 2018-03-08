package ru.ra66it.updaterforspotify.mvp.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.mvp.view.IntroView;
import ru.ra66it.updaterforspotify.storage.QueryPreferences;

/**
 * Created by 2Rabbit on 08.03.2018.
 */

public class IntroActivityPresenter {

    private IntroView mView;
    private QueryPreferences queryPreferences;

    public IntroActivityPresenter(IntroView mView, QueryPreferences queryPreferences) {
        this.mView = mView;
        this.queryPreferences = queryPreferences;
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            queryPreferences.setFirstLaunch(false);
            mView.finish();
        } else {
            mView.getPermission();
        }
    }

}
