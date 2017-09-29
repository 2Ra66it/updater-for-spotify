package ru.ra66it.updaterforspotify.notification;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import ru.ra66it.updaterforspotify.QueryPreferneces;
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

        int requestCode = intent.getIntExtra(SpotifyService.REQUEST_CODE, 0);
        Notification notification = (Notification)
                intent.getParcelableExtra(SpotifyService.NOTIFICATION);

        //if update available - show notification
        if (UtilsSpotify.isUpdateAvailable(UtilsSpotify.getInstalledSpotifyVersion(context),
                QueryPreferneces.getLatestVersion(context))) {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(requestCode, notification);
        }

    }
}
