package ru.ra66it.updaterforspotify.mvp.presenter;


import android.content.Context;


import ru.ra66it.updaterforspotify.notification.PollService;
import ru.ra66it.updaterforspotify.storage.QueryPreferneces;
import ru.ra66it.updaterforspotify.mvp.view.MainBaseView;


/**
 * Created by 2Rabbit on 12.11.2017.
 */

public class MainActivityPresenter {

    private MainBaseView baseView;

    public MainActivityPresenter(MainBaseView baseView) {
        this.baseView = baseView;
    }

    public void startIntroActivity(Context context) {
        if (QueryPreferneces.isFirstLaunch(context)) {
            baseView.startIntroActivity();
        }
    }

    public void startNotification(Context context) {
        if (QueryPreferneces.getNotificationOrigin(context)) {
            PollService.setServiceAlarm(context, QueryPreferneces.getNotificationOrigin(context));
        } else {
            PollService.setServiceAlarm(context, false);
        }
    }


}
