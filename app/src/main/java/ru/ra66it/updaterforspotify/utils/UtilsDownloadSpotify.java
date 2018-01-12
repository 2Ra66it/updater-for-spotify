package ru.ra66it.updaterforspotify.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import ru.ra66it.updaterforspotify.R;


/**
 * Created by 2Rabbit on 01.10.2017.
 */

public class UtilsDownloadSpotify {

    public static void downloadSpotify(Context context, String url, String name) {
        //Remove unnecessary characters in the link
        String fullUrl = url.split(": ")[1];
        String fullName = name.replace(" ", "_").replaceAll("\\.", "_");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fullUrl));
        request.setTitle(context.getString(R.string.downloading) + " " + name);
        request.setDescription(context.getString(R.string.downloading_in));
        request.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                fullName + ".apk");

        DownloadManager manager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(context, name + " is downloading", Toast.LENGTH_SHORT).show();
    }
}
