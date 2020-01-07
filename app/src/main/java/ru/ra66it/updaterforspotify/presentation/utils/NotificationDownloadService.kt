package ru.ra66it.updaterforspotify.presentation.utils

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import ru.ra66it.updaterforspotify.actionDownload
import ru.ra66it.updaterforspotify.latestLinkKey
import ru.ra66it.updaterforspotify.latestVersionKey
import ru.ra66it.updaterforspotify.notificationIdKey


/**
 * Created by 2Rabbit on 30.09.2017.
 */

class NotificationDownloadService : IntentService(NotificationDownloadService::class.java.simpleName) {

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val action = it.action
            val link = it.getStringExtra(latestLinkKey)
            val id = it.getIntExtra(notificationIdKey, 0)
            val version = it.getStringExtra(latestVersionKey)
            if (actionDownload == action && !link.isNullOrEmpty() && !version.isNullOrEmpty()) {
                //Hide Notification
                val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(id)
                UtilsDownloadSpotify.downloadSpotify(link, version)
            }
        }
    }
}
