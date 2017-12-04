package ru.ra66it.updaterforspotify.mvp.presenter;


import android.content.Context;


import ru.ra66it.updaterforspotify.rest.SpotifyApi;
import ru.ra66it.updaterforspotify.storage.QueryPreferneces;
import ru.ra66it.updaterforspotify.mvp.view.MainBaseView;
import ru.ra66it.updaterforspotify.notification.PollService;


/**
 * Created by 2Rabbit on 12.11.2017.
 */

public class MainActivityPresenter {

    private MainBaseView baseView;
    private SpotifyApi spotifyApi;

    public MainActivityPresenter(MainBaseView baseView) {
        this.baseView = baseView;
        this.spotifyApi = spotifyApi;
    }

    public void initViewPager(Context context) {
        if (QueryPreferneces.isFirstLaunch(context)) {
            baseView.startIntroActivity();
        } else {
            baseView.initViewPager();
        }
    }



    public void startNotification(Context context) {
        if (QueryPreferneces.getNotificationDogFood(context)) {
            PollService.setServiceAlarm(context,
                    QueryPreferneces.getNotificationDogFood(context));
        } else if (QueryPreferneces.getNotificationOrigin(context)) {
            PollService.setServiceAlarm(context,
                    QueryPreferneces.getNotificationOrigin(context));
        }
    }


}
