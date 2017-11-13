package ru.ra66it.updaterforspotify.notification;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import ru.ra66it.updaterforspotify.utils.UtilsSpotify;


/**
 * Created by 2Rabbit on 28.09.2017.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received result: " + getResultCode());
        if (getResultCode() != Activity.RESULT_OK) {
            return;
        }

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        String latestVersion = intent.getStringExtra(PollService.LATEST_VERSION);

        Notification notification = (Notification)
                intent.getParcelableExtra(PollService.NOTIFICATION);


        if(requestCode == 0 && UtilsSpotify.isDogfoodUpdateAvailable(
                UtilsSpotify.getInstalledSpotifyVersion(context), latestVersion)) {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat
                    .from(context);
            notificationManagerCompat.notify(requestCode, notification);
        }


        if(requestCode == 1 && UtilsSpotify.isSpotifyUpdateAvailable(
                UtilsSpotify.getInstalledSpotifyVersion(context), latestVersion)) {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat
                    .from(context);
            notificationManagerCompat.notify(requestCode, notification);
        }


    }
}
