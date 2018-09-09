package ru.ra66it.updaterforspotify.presentation.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent

import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify


/**
 * Created by 2Rabbit on 30.09.2017.
 */

class NotificationDownloadService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        val link = intent.getStringExtra(PollService.LATEST_LINK)
        val name = intent.getStringExtra(PollService.LATEST_VERSION_NAME)
        val id = intent.getIntExtra(PollService.NOTIFICATION_ID, 0)
        if (actionDownload == action) {
            //Hide Notification
            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(id)
            UtilsDownloadSpotify.downloadSpotify(link, name)
        }
    }

    companion object {
        private const val TAG = "NotificationDownloadService"
        const val actionDownload = "actionDownload"
    }
}
