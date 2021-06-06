package ru.ra66it.updaterforspotify.presentation.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp

object UtilsDownloadSpotify {

    fun downloadSpotify(url: String, version: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        val name = StringService.getById(R.string.spotify) + " $version"
        val uriApk = Uri.parse(
            "file://" + Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ) + "/$name.apk"
        )

        request.setTitle(StringService.getById(R.string.downloading) + " $name")
        request.setDescription(StringService.getById(R.string.downloading_in))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationUri(uriApk)

        val manager =
            UpdaterApp.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}