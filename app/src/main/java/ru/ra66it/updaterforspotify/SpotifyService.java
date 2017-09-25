package ru.ra66it.updaterforspotify;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ru.ra66it.updaterforspotify.api.SpotifyDogfoodApi;
import ru.ra66it.updaterforspotify.utils.UtilsSpotify;

/**
 * Created by 2Rabbit on 25.09.2017.
 */

public class SpotifyService extends IntentService {

    private static final String TAG = "SpotifyService";

    private static final long POLL_INTERVAL = TimeUnit.HOURS.toMillis(24);

    public static Intent newIntent(Context context) {
        return new Intent(context, SpotifyService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = SpotifyService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    POLL_INTERVAL, pi);
        }else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = SpotifyService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }


    public SpotifyService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        String latestVersion = null;

        try {

            if (SpotifyDogfoodApi.Factory.getInstance().getLatest().execute().body().getTagName() != null) {
                latestVersion = SpotifyDogfoodApi.Factory.getInstance().getLatest().execute().body().getTagName();
                QueryPreferneces.setLatestVersion(this, latestVersion);
            } else {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        Resources resources = getResources();
        Intent i = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        if (UtilsSpotify.isUpdateAvailable(UtilsSpotify.getInstalledSpotifyVersion(this),
                QueryPreferneces.getLatestVersion(this))) {

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("Updater for Spotify")
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentText("New version " + QueryPreferneces.getLatestVersion(this) + " available!")
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);

        }


    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
     }
}
