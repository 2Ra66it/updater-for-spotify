package ru.ra66it.updaterforspotify.presentation.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp


/**
 * Created by 2Rabbit on 01.10.2017.
 */

object UtilsDownloadSpotify {

    fun downloadSpotify(url: String, name: String) {
        val fullName = name.replace(" ", "_").replace("\\.".toRegex(), "_")

        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(StringService.getById(R.string.downloading) + " " + name)
        request.setDescription(StringService.getById(R.string.downloading_in))
        request.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        uri = Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/$fullName.apk")
        request.setDestinationUri(uri)

        val manager = UpdaterApp.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        Toast.makeText(UpdaterApp.instance, "$name is downloading", Toast.LENGTH_SHORT).show()
    }

    lateinit var uri: Uri
}
