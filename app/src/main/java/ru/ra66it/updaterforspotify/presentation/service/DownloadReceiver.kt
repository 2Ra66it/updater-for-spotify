package ru.ra66it.updaterforspotify.presentation.service

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.content.FileProvider
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify.uri
import java.io.File


class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val install =  Intent(Intent.ACTION_INSTALL_PACKAGE)
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                install.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val content = FileProvider.getUriForFile(context, context.packageName + ".provider",  File(uri.path))
                install.data = content
                context.startActivity(install)
            } else {
                val install = Intent(Intent.ACTION_VIEW)
                install.setDataAndType(uri, "application/vnd.android.package-archive")
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(install)
            }
        }
    }
}
