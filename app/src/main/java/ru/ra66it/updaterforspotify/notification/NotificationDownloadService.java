package ru.ra66it.updaterforspotify.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import ru.ra66it.updaterforspotify.utils.UtilsDownloadSpotify;


/**
 * Created by 2Rabbit on 30.09.2017.
 */

public class NotificationDownloadService extends IntentService {
    private static final String TAG = "NotificationDownloadService";
    public static final String ACTION_DOWNLOAD = "ACTION_DOWNLOAD";

    public NotificationDownloadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String action = intent.getAction();
        final String link = intent.getStringExtra(PollService.LATEST_LINK);
        final String name = intent.getStringExtra(PollService.LATEST_VERSION_NAME);
        int  id = intent.getIntExtra(PollService.NOTIFICATION_ID, 0);
        if (ACTION_DOWNLOAD.equals(action)) {
            //Hide Notification
            NotificationManager manager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(id);
            UtilsDownloadSpotify.downloadSpotify(link, name);
        }
    }
}
