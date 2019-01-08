package ru.ra66it.updaterforspotify.presentation.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.uriPath


/**
 * Created by 2Rabbit on 01.10.2017.
 */

object UtilsDownloadSpotify {

    fun downloadSpotify(url: String) {
        val name = StringService.getById(R.string.spotify)
        val request = DownloadManager.Request(Uri.parse(url))

        request.setTitle(StringService.getById(R.string.downloading) + " $name")
        request.setDescription(StringService.getById(R.string.downloading_in))
        request.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationUri(Uri.parse(uriPath))

        val manager = UpdaterApp.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}
