package ru.ra66it.updaterforspotify.domain.usecase

import android.content.pm.PackageManager
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.data.repositories.UpdaterRepository
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.domain.transformer.UpdaterTransformer
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.utils.VersionsComparator
import ru.ra66it.updaterforspotify.spotifyPackage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdaterUseCase @Inject constructor(
    private val updaterRepository: UpdaterRepository,
    private val updaterTransformer: UpdaterTransformer,
    private val versionsComparator: VersionsComparator
) {

    suspend fun getSpotifyData(): SpotifyData {
        return updaterRepository.getSpotify().let {
            updaterTransformer.transform(it, getSpotifyInstalledVersion(), versionsComparator)
        }
    }

    fun haveUpdate(data: SpotifyData): Boolean {
        val installedVersion = getSpotifyInstalledVersion()
        return installedVersion.isEmpty() || versionsComparator.compareVersion(
            installedVersion,
            data.latestVersionNumber
        ) == 1
    }

    fun updateSpotifyData(
        data: SpotifyData,
    ): SpotifyData {
        val installedSpotifyVersion = getSpotifyInstalledVersion()

        val state = updaterTransformer.getSpotifyState(
            installedSpotifyVersion = installedSpotifyVersion,
            latestVersion = data.latestVersionNumber,
            versionsComparator = versionsComparator
        )

        return data.copy(
            installedVersion = updaterTransformer.transformInstalledVersion(installedSpotifyVersion),
            spotifyState = state
        )
    }


    fun getSpotifyVersion(): String {
        val spotifyInstalledVersion = getSpotifyInstalledVersion()
        return if (spotifyInstalledVersion.isEmpty()) StringService.getById(R.string.spotify_not_installed) else spotifyInstalledVersion
    }

    private fun getSpotifyInstalledVersion(): String {
        return try {
            UpdaterApp.instance.packageManager.getPackageInfo(spotifyPackage, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }
}
