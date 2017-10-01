package ru.ra66it.updaterforspotify.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import ru.ra66it.updaterforspotify.QueryPreferneces;
import ru.ra66it.updaterforspotify.R;

/**
 * Created by 2Rabbit on 01.10.2017.
 */

public class UtilsDownloadSpotify {

    public static void downloadSpotify(Context context, String url) {
        //Remove unnecessary characters in the link
        String fullUrl = url.split(": ")[1];

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fullUrl));
        request.setTitle(context.getString(R.string.downloading_spotify));
        request.setDescription(context.getString(R.string.downloading_in));
        request.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                QueryPreferneces.getLatestVersionName(context) + ".apk");

        DownloadManager manager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
