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

public class PollService extends IntentService {

    private static final String TAG = "PollService";

    private static final long POLL_INTERVAL = TimeUnit.DAYS.toMillis(1);

    public static final String ACTION_SHOW_NOTIFICATION =
            "ru.ra66it.android.updaterforspotify.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE =
            "ru.ra66it.android.updaterforspotify.PRIVATE";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";


    public static final String LATEST_LINK = "latest_link";
    public static final String LATEST_VERSION = "latest_version";
    public static final String LATEST_VERSION_NAME = "latest_version_name";


    private String latestVersion;
    private String latestVersionName;
    private String latestLink;


    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = PollService.newIntent(context);
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
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }


    public PollService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);
        if (!isNetworkAvailableAndConnected()) {
            return;
        }


        try {
            if (SpotifyDogfoodApi.Factory.getInstance().getLatest().execute().body().getTagName() != null) {
                latestVersion = SpotifyDogfoodApi.Factory.getInstance().getLatest().execute().body().getTagName();
                latestVersionName = SpotifyDogfoodApi.Factory.getInstance().getLatest().execute().body().getName();
                latestLink = SpotifyDogfoodApi.Factory.getInstance().getLatest().execute().body().getBody();

                //Launch app
                Resources resources = getResources();
                Intent i = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

                //Download spotify
                Intent intentDownload = new Intent(this, NotificationDownloadService.class);
                intentDownload.setAction(NotificationDownloadService.ACTION_DOWNLOAD);
                intentDownload.putExtra(LATEST_LINK, latestLink);
                PendingIntent piDownload = PendingIntent.getService(this, 0, intentDownload, PendingIntent.FLAG_UPDATE_CURRENT);


                Notification notification = new NotificationCompat.Builder(this)
                        .setTicker(resources.getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_notification)
                        .setContentTitle(getString(R.string.update_available))
                        .setContentText("New version Spotify Dogfood " + latestVersion + " available!")
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .addAction(R.drawable.ic_file_download_black_24dp, "INSTALL NOW", piDownload)
                        .build();


                showBackgroundNotification(0, notification, latestVersion);
            } else {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }





    }

    private void showBackgroundNotification(int requestCode, Notification notification, String latestVersion) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        i.putExtra(LATEST_VERSION, latestVersion);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
