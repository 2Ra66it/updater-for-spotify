package ru.ra66it.updaterforspotify.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.model.Spotify;
import ru.ra66it.updaterforspotify.rest.SpotifyApi;
import ru.ra66it.updaterforspotify.storage.QueryPreferneces;
import ru.ra66it.updaterforspotify.ui.activity.MainActivity;
import ru.ra66it.updaterforspotify.utils.UtilsSpotify;

/**
 * Created by 2Rabbit on 07.01.2018.
 */


public class PollService extends JobService {

    private static final String TAG = PollService.class.getSimpleName();
    private static final long POLL_INTERVAL = TimeUnit.DAYS.toMillis(1);
    private static final int JOB_ID = 123;
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_CHANEL = "UFS_CHANEL_ID";
    public static final String LATEST_LINK = "latest_link";
    public static final String LATEST_VERSION = "latest_version";
    public static final String LATEST_VERSION_NAME = "latest_version_name";
    private String latestVersion;
    private String latestVersionName;
    private String latestLink;

    private CompositeDisposable compositeDisposable;

    @Inject
    SpotifyApi spotifyApi;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.getApplicationComponent().inject(this);
        Log.i(TAG, "Received an JobService");
        compositeDisposable = new CompositeDisposable();
    }


    public static void setServiceAlarm(Context context, boolean isOn) {
        ComponentName component = new ComponentName(context, PollService.class);
        JobInfo builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder = new JobInfo.Builder(JOB_ID, component)
                    .setMinimumLatency(POLL_INTERVAL)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
        } else {
            builder = new JobInfo.Builder(JOB_ID, component)
                    .setPeriodic(POLL_INTERVAL)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
        }

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (isOn) {
            jobScheduler.schedule(builder);
        } else {
            jobScheduler.cancelAll();
        }

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if (QueryPreferneces.getNotificationDogFood(this)) {
            notificationsSpotifyDF(jobParameters);
        } else if (QueryPreferneces.getNotificationOrigin(this)) {
            if (QueryPreferneces.isSpotifyBeta(this)) {
                notificationsSpotifyOrigBeta(jobParameters);
            } else {
                notificationsSpotifyOrig(jobParameters);
            }
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private void notificationsSpotifyDF(JobParameters jobParameters) {
        spotifyApi.getLatestDogFood()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Spotify>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        latestLink = null;
                        latestVersionName = null;
                        latestVersion = null;
                    }

                    @Override
                    public void onNext(Spotify spotify) {
                        latestLink = spotify.getBody();
                        latestVersionName = spotify.getName();
                        latestVersion = spotify.getTagName();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {
                        makeNotification(0);
                        jobFinished(jobParameters, false);
                    }
                });
    }

    private void notificationsSpotifyOrig(JobParameters jobParameters) {
        spotifyApi.getLatestOrigin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Spotify>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        latestLink = null;
                        latestVersionName = null;
                        latestVersion = null;
                    }

                    @Override
                    public void onNext(Spotify spotify) {
                        latestLink = spotify.getBody();
                        latestVersionName = spotify.getName();
                        latestVersion = spotify.getTagName();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {
                        makeNotification(1);
                        jobFinished(jobParameters, false);
                    }
                });
    }

    private void notificationsSpotifyOrigBeta(JobParameters jobParameters) {
        spotifyApi.getLatestOriginBeta()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Spotify>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        latestLink = null;
                        latestVersionName = null;
                        latestVersion = null;
                    }

                    @Override
                    public void onNext(Spotify spotify) {
                        latestLink = spotify.getBody();
                        latestVersionName = spotify.getName();
                        latestVersion = spotify.getTagName();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {
                        makeNotification(1);
                        jobFinished(jobParameters, false);
                    }
                });
    }

    private void makeNotification(int notificationId) {
        //Launch app
        Resources resources = getResources();
        Intent i = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        //Download spotify
        Intent intentDownload = new Intent(this, NotificationDownloadService.class);
        intentDownload.setAction(NotificationDownloadService.ACTION_DOWNLOAD);
        intentDownload.putExtra(LATEST_LINK, latestLink);
        intentDownload.putExtra(LATEST_VERSION_NAME, latestVersionName);
        intentDownload.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent piDownload = PendingIntent.getService(this, notificationId, intentDownload,
                PendingIntent.FLAG_UPDATE_CURRENT);


        //Notification
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANEL)
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(getString(R.string.update_available))
                .setContentText(getString(R.string.new_version) + " "
                        + latestVersionName + " " + getString(R.string.available))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.colorAccent))
                .addAction(R.drawable.ic_file_download_black_24dp,
                        getString(R.string.install_now), piDownload)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        makeNotificationChanel(notificationManager);

        showNotification(notificationId, notificationManager, notification);

    }

    private void makeNotificationChanel(NotificationManager notificationManager) {
        //Show notification on Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANEL, getString(R.string.notificaion_chanel_name), NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(int notificationId, NotificationManager notificationManager, Notification notification) {
        if (notificationId == 0 && UtilsSpotify.isDogfoodUpdateAvailable(
                UtilsSpotify.getInstalledSpotifyVersion(this), latestVersion)) {

            notificationManager.notify(notificationId, notification);
        } else if (notificationId == 1 && UtilsSpotify.isSpotifyUpdateAvailable(
                UtilsSpotify.getInstalledSpotifyVersion(this), latestVersion)) {

            notificationManager.notify(notificationId, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
