package ru.ra66it.updaterforspotify.presentation.utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import ru.ra66it.updaterforspotify.actionDownload
import ru.ra66it.updaterforspotify.latestLinkKey
import ru.ra66it.updaterforspotify.latestVersionKey
import ru.ra66it.updaterforspotify.notificationIdKey

class NotificationDownloadService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val action = intent.action
        val link = intent.getStringExtra(latestLinkKey)
        val id = intent.getIntExtra(notificationIdKey, 0)
        val version = intent.getStringExtra(latestVersionKey)
        if (actionDownload == action && !link.isNullOrEmpty() && !version.isNullOrEmpty()) {
            //Hide Notification
            val manager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(id)
            UtilsDownloadSpotify.downloadSpotify(link, version)
        }
    }
}
