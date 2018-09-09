package ru.ra66it.updaterforspotify.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.model.FullSpotifyModel
import ru.ra66it.updaterforspotify.presentation.ui.activity.MainActivity
import ru.ra66it.updaterforspotify.presentation.utils.UtilsSpotify
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by 2Rabbit on 07.01.2018.
 */


class PollService : JobService() {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var spotifyInteractor: SpotifyInteractor
    @Inject
    lateinit var queryPreferences: QueryPreferences

    override fun onCreate() {
        super.onCreate()
        UpdaterApp.applicationComponent.inject(this)
        Log.i(TAG, "Received an JobService")
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        if (queryPreferences.isEnableNotification) {
            notificationSpotify(jobParameters)
        }
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return true
    }

    private fun notificationSpotify(jobParameters: JobParameters) {
        compositeDisposable.add(spotifyInteractor.latestSpotify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ spotify ->
                    makeNotification(1, FullSpotifyModel(spotify))
                    jobFinished(jobParameters, false)
                }, { throwable ->
                    Log.i(TAG, throwable.message)
                }))
    }

    private fun makeNotification(notificationId: Int, fullSpotifyModel: FullSpotifyModel) {
        //Launch app
        val resources = UpdaterApp.instance.resources
        val i = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(this, 0, i, 0)

        //Download spotify
        val intentDownload = Intent(this, NotificationDownloadService::class.java)
        intentDownload.action = NotificationDownloadService.actionDownload
        intentDownload.putExtra(LATEST_LINK, fullSpotifyModel.latestLink)
        intentDownload.putExtra(LATEST_VERSION_NAME, fullSpotifyModel.latestVersionName)
        intentDownload.putExtra(NOTIFICATION_ID, notificationId)
        val piDownload = PendingIntent.getService(this, notificationId, intentDownload,
                PendingIntent.FLAG_UPDATE_CURRENT)

        //Notification
        val builder = NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(getString(R.string.update_available))
                .setContentText(getString(R.string.new_version) + " "
                        + fullSpotifyModel.latestVersionName + " " + getString(R.string.available))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .addAction(R.drawable.ic_file_download_black_24dp,
                        getString(R.string.install_now), piDownload)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        makeNotificationChanel(notificationManager, builder)

        showNotification(fullSpotifyModel.latestVersionNumber, notificationId, notificationManager, builder.build())
    }

    private fun makeNotificationChanel(notificationManager: NotificationManager,
                                       builder: NotificationCompat.Builder) {
        //Show notification on Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANEL,
                    getString(R.string.notificaion_chanel_name), NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            builder.setChannelId(NOTIFICATION_CHANEL)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(latestVersion: String, notificationId: Int,
                                 notificationManager: NotificationManager, notification: Notification) {
        if (!UtilsSpotify.isSpotifyInstalled || UtilsSpotify.isSpotifyUpdateAvailable(
                        UtilsSpotify.installedSpotifyVersion, latestVersion)) {
            notificationManager.notify(notificationId, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    companion object {
        private val TAG = PollService::class.java.simpleName
        private val POLL_INTERVAL = TimeUnit.DAYS.toMillis(1)
        private const val JOB_ID = 123
        const val NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION_CHANEL = "ufs_chanel_id"
        const val LATEST_LINK = "latest_link"
        const val LATEST_VERSION_NAME = "latest_version_name"

        fun setServiceAlarm(isOn: Boolean) {
            val component = ComponentName(UpdaterApp.instance, PollService::class.java)
            val builder: JobInfo

            builder = JobInfo.Builder(JOB_ID, component)
                    .setPeriodic(POLL_INTERVAL)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build()

            val jobScheduler = UpdaterApp.instance.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            if (isOn) {
                if (jobScheduler.allPendingJobs.size == 0) {
                    jobScheduler.schedule(builder)
                }
            } else {
                jobScheduler.cancelAll()
            }
        }
    }
}
