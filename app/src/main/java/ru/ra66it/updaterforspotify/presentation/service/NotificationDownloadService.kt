package ru.ra66it.updaterforspotify.presentation.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import ru.ra66it.updaterforspotify.actionDownload
import ru.ra66it.updaterforspotify.latestLinkKey
import ru.ra66it.updaterforspotify.latestVersionKey
import ru.ra66it.updaterforspotify.notificationIdKey
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify


/**
 * Created by 2Rabbit on 30.09.2017.
 */

class NotificationDownloadService : IntentService(NotificationDownloadService::class.java.simpleName) {

    override fun onHandleIntent(intent: Intent) {
        val action = intent.action
        val link = intent.getStringExtra(latestLinkKey)
        val id = intent.getIntExtra(notificationIdKey, 0)
        val version = intent.getStringExtra(latestVersionKey)
        if (actionDownload == action) {
            //Hide Notification
            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(id)
            UtilsDownloadSpotify.downloadSpotify(link, version)
        }
    }
}
