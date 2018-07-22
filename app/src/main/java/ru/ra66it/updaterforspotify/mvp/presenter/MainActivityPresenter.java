package ru.ra66it.updaterforspotify.mvp.presenter;


import ru.ra66it.updaterforspotify.mvp.view.MainBaseView;
import ru.ra66it.updaterforspotify.notification.PollService;
import ru.ra66it.updaterforspotify.storage.QueryPreferences;


/**
 * Created by 2Rabbit on 12.11.2017.
 */

public class MainActivityPresenter {

    private MainBaseView mView;
    private QueryPreferences queryPreferences;

    public MainActivityPresenter(MainBaseView mView, QueryPreferences queryPreferences) {
        this.mView = mView;
        this.queryPreferences = queryPreferences;
    }

    public void startIntroActivity() {
        if (queryPreferences.isFirstLaunch()) {
            mView.startIntroActivity();
        }
    }

    public void startNotification() {
        if (queryPreferences.getNotifications()) {
            PollService.setServiceAlarm(queryPreferences.getNotifications());
        } else {
            PollService.setServiceAlarm(false);
        }
    }


}
