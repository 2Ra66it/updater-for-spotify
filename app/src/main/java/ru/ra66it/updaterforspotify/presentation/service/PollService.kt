package ru.ra66it.updaterforspotify.presentation.service

import android.Manifest
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
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.model.Result
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.presentation.ui.activity.MainActivity
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper
import ru.ra66it.updaterforspotify.presentation.utils.UtilsSpotify
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by 2Rabbit on 07.01.2018.
 */

class PollService : JobService() {
    private var job: Job? = null

    @Inject
    lateinit var spotifyInteractor: SpotifyInteractor

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var spotifyMapper: SpotifyMapper

    override fun onCreate() {
        super.onCreate()
        UpdaterApp.applicationComponent.inject(this)
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        if (sharedPreferencesHelper.isEnableNotification) {
            notificationSpotify(jobParameters)
        }
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return true
    }

    private fun notificationSpotify(jobParameters: JobParameters) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val result = spotifyInteractor.getSpotify()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        val data = spotifyMapper.map(result.data)
                        makeNotification(data)
                        jobFinished(jobParameters, false)
                    }
                    is Result.Error -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, result.exception.message)
                        }
                    }
                }
            }
        }
    }

    private fun makeNotification(spotifyModel: SpotifyData) {
        //Launch app
        val resources = UpdaterApp.instance.resources
        val i = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(this, 0, i, 0)

        //Notification
        val contentText = getString(R.string.new_version) + " " +
                spotifyModel.latestVersionName + " " + getString(R.string.available)

        val havePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        val builder = NotificationCompat.Builder(this, notificationChanelId)
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(getString(R.string.update_available))
                .setContentText(contentText)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))

        if (havePermission) {
            //Download spotify
            val intentDownload = Intent(this, NotificationDownloadService::class.java)
            intentDownload.action = actionDownload
            intentDownload.putExtra(latestLinkKey, spotifyModel.latestLink)
            intentDownload.putExtra(latestVersionKey, spotifyModel.latestVersionNumber)
            intentDownload.putExtra(notificationIdKey, notificationId)
            val piDownload = PendingIntent.getService(this, 0, intentDownload,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(R.drawable.ic_file_download_black_24dp,
                    getString(R.string.install_now), piDownload)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        makeNotificationChanel(notificationManager, builder)

        showNotification(spotifyModel.latestVersionNumber, notificationManager, builder.build())
    }

    private fun makeNotificationChanel(notificationManager: NotificationManager,
                                       builder: NotificationCompat.Builder) {
        //Show notification on Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(notificationChanelId,
                    getString(R.string.notificaion_chanel_name), NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            builder.setChannelId(notificationChanelId)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(latestVersion: String, notificationManager: NotificationManager, notification: Notification) {
        if (!UtilsSpotify.isSpotifyInstalled || UtilsSpotify.isSpotifyUpdateAvailable(
                        UtilsSpotify.installedSpotifyVersion, latestVersion)) {
            notificationManager.notify(notificationId, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        private val TAG = PollService::class.java.simpleName

        fun setServiceAlarm(isOn: Boolean, checkInterval: Long) {
            val context = UpdaterApp.instance
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val poolInterval = TimeUnit.DAYS.toMillis(checkInterval)

            if (isOn) {
                val needSchedule = jobScheduler.allPendingJobs.firstOrNull()?.intervalMillis != poolInterval
                if (needSchedule) {
                    val component = ComponentName(context, PollService::class.java)
                    val builder = JobInfo.Builder(jobId, component)
                            .setPeriodic(poolInterval)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setPersisted(true)
                            .build()

                    jobScheduler.schedule(builder)
                }
            } else {
                if (jobScheduler.allPendingJobs.size != 0) {
                    jobScheduler.cancelAll()
                }
            }
        }
    }
}
