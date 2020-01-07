package ru.ra66it.updaterforspotify.presentation.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.presentation.ui.activity.MainActivity
import ru.ra66it.updaterforspotify.presentation.utils.NotificationDownloadService
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper
import ru.ra66it.updaterforspotify.presentation.utils.UtilsSpotify
import javax.inject.Inject

class CheckingWorker(val context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var spotifyInteractor: SpotifyInteractor
    @Inject
    lateinit var spotifyMapper: SpotifyMapper

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        UpdaterApp.applicationComponent.inject(this)
    }

    override suspend fun doWork(): Result = coroutineScope {
        when (val result = withContext(Dispatchers.IO) { spotifyInteractor.getSpotify() }) {
            is ru.ra66it.updaterforspotify.domain.model.Result.Success -> {
                val data = spotifyMapper.map(result.data)

                val haveUpdate = !UtilsSpotify.isSpotifyInstalled || UtilsSpotify.isSpotifyUpdateAvailable(
                        UtilsSpotify.installedSpotifyVersion, data.latestVersionNumber)

                if (haveUpdate) {
                    val notification = makeNotification(data)
                    notificationManager.notify(notificationId, notification)
                }

                Result.success()
            }
            is ru.ra66it.updaterforspotify.domain.model.Result.Error -> {
                if (BuildConfig.DEBUG) {
                    result.exception.printStackTrace()
                }
                Result.failure()
            }
        }
    }

    private fun makeNotification(spotifyModel: SpotifyData): Notification {
        //Launch app
        val resources = UpdaterApp.instance.resources
        val i = Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pi = PendingIntent.getActivity(context, 0, i, 0)

        //Notification
        val contentText = context.getString(R.string.new_version) + " " +
                spotifyModel.latestVersionName + " " + context.getString(R.string.available)

        val builder = NotificationCompat.Builder(context, notificationChanelId)
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_autorenew_black_24dp)
                .setContentTitle(context.getString(R.string.update_available))
                .setContentText(contentText)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle())
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))

        if (UtilsSpotify.haveStoragePermission(context)) {
            //Download spotify
            val intentDownload = Intent(context, NotificationDownloadService::class.java)
            intentDownload.action = actionDownload
            intentDownload.putExtra(latestLinkKey, spotifyModel.latestLink)
            intentDownload.putExtra(latestVersionKey, spotifyModel.latestVersionNumber)
            intentDownload.putExtra(notificationIdKey, notificationId)
            val piDownload = PendingIntent.getService(context, 0, intentDownload,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(R.drawable.ic_file_download_black_24dp,
                    context.getString(R.string.download), piDownload)
        }

        makeNotificationChanel(builder)

        return builder.build()
    }

    private fun makeNotificationChanel(builder: NotificationCompat.Builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(notificationChanelId,
                    context.getString(R.string.notificaion_chanel_name), NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            builder.setChannelId(notificationChanelId)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        val TAG = CheckingWorker::class.java.simpleName
    }
}