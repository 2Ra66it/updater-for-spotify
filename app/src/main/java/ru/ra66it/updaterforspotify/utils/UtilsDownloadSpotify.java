package ru.ra66it.updaterforspotify.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.R;


/**
 * Created by 2Rabbit on 01.10.2017.
 */

public class UtilsDownloadSpotify {

    public static void downloadSpotify(String url, String name) {
        String fullName = name.replace(" ", "_").replaceAll("\\.", "_");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(StringService.getById(R.string.downloading) + " " + name);
        request.setDescription(StringService.getById(R.string.downloading_in));
        request.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                fullName + ".apk");

        DownloadManager manager = (DownloadManager) MyApplication.getContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(MyApplication.getContext(), name + " is downloading", Toast.LENGTH_SHORT).show();
    }
}
