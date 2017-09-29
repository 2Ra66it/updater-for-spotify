package ru.ra66it.updaterforspotify.notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import ru.ra66it.updaterforspotify.MainActivity;
import ru.ra66it.updaterforspotify.QueryPreferneces;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.api.SpotifyDogfoodApi;

/**
 * Created by 2Rabbit on 25.09.2017.
 */

public class SpotifyService extends IntentService {

    private static final String TAG = "SpotifyService";

    private static final long POLL_INTERVAL = TimeUnit.DAYS.toMillis(1);

    public static final String ACTION_SHOW_NOTIFICATION =
            "ru.ra66it.android.updaterforspotify.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE =
            "ru.ra66it.android.updaterforspotify.PRIVATE";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

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
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }


        QueryPreferneces.setAlarmOn(context, isOn);
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


        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentText("New version Spotify Dogfood " + QueryPreferneces.getLatestVersion(this) + " available!")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();


        showBackgroundNotification(0, notification);


    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
