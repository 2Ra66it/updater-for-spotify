package ru.ra66it.updaterforspotify.domain.usecase

import android.content.pm.PackageManager
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.data.repositories.UpdaterRepository
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.domain.transformer.UpdaterTransformer
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class UpdaterUseCase @Inject constructor(
    private val updaterRepository: UpdaterRepository,
    private val updaterTransformer: UpdaterTransformer,
) {

    suspend fun getSpotifyData(): SpotifyData {
        return updaterRepository.getSpotify().let {
            updateInstalledVersion(updaterTransformer.transform(it))
        }
    }

    fun updateInstalledVersion(spotifyData: SpotifyData): SpotifyData {
        val spotifyState = getSpotifyState(spotifyData.latestVersionNumber)
        return spotifyData.copy(
            installedVersion = installedSpotifyVersion,
            spotifyState = spotifyState
        )
    }

    fun haveUpdate(data: SpotifyData): Boolean {
        return isSpotifyInstalled.not() || isSpotifyUpdateAvailable(
            installedSpotifyVersion,
            data.latestVersionNumber
        )
    }

    private fun getSpotifyState(latestVersion: String): Int {
        return if (isSpotifyInstalled) {
            val installedVersion =
                UpdaterApp.instance.packageManager.getPackageInfo(spotifyPackage, 0).versionName
            val haveUpdate = isSpotifyUpdateAvailable(installedVersion, latestVersion)

            if (haveUpdate) {
                spotifyHaveUpdate
            } else {
                spotifyIsLatest
            }

        } else {
            spotifyNotInstalled
        }
    }

    private val isSpotifyInstalled: Boolean
        get() {
            return try {
                UpdaterApp.instance.packageManager.getPackageInfo(spotifyPackage, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    private val installedSpotifyVersion: String
        get() {
            return try {
                UpdaterApp.instance.packageManager.getPackageInfo(spotifyPackage, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                StringService.getById(R.string.spotify_not_installed)
            }
        }

    private fun isSpotifyUpdateAvailable(installVersion: String, lastVersion: String): Boolean {
        return compareVersion(installVersion, lastVersion) == 1
    }

    private fun compareVersion(installVersion: String, lastVersion: String): Int {
        val firstVersion = installVersion.split("\\.".toRegex())
        val secondVersion = lastVersion.split("\\.".toRegex())
        val length = max(firstVersion.size, secondVersion.size)

        for (i in 0 until length) {
            val firstPart = if (i < firstVersion.size) firstVersion[i].toInt() else 0
            val secondPart = if (i < secondVersion.size) secondVersion[i].toInt() else 0
            when {
                firstPart > secondPart -> return -1
                firstPart < secondPart -> return 1
            }
        }

        return 0
    }
}
