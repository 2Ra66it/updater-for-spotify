package ru.ra66it.updaterforspotify

import android.os.Environment
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import java.util.concurrent.TimeUnit


const val spotifyPackage = "com.spotify.music"
const val notificationIdKey = "notificationIdKey"
const val latestLinkKey = "latestLinkKey"
const val notificationChanelId = "ufsChanelId"
const val jobId = 322
const val notificationId = 228
const val sharedPreferencesName = "ufsPreferences"
const val actionDownload = "actionDownload"

val poolInterval = TimeUnit.DAYS.toMillis(1)
val uriPath = "file://" + Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS).toString() + "/" + StringService.getById(R.string.spotify) + ".apk"

