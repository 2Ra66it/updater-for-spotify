package ru.ra66it.updaterforspotify.presentation.mvp.presenter

import kotlinx.coroutines.*
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences
import ru.ra66it.updaterforspotify.domain.Result
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.model.FullSpotifyModel
import ru.ra66it.updaterforspotify.presentation.mvp.view.SpotifyView
import ru.ra66it.updaterforspotify.presentation.service.PollService
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify
import ru.ra66it.updaterforspotify.presentation.utils.UtilsSpotify

/**
 * Created by 2Rabbit on 11.11.2017.
 */

class SpotifyPresenter(private val spotifyInteractor: SpotifyInteractor,
                       private val queryPreferences: QueryPreferences) {

    private lateinit var view: SpotifyView
    private var fullSpotifyModel: FullSpotifyModel? = null
    private var job: Job? = null

    fun setView(mView: SpotifyView) {
        this.view = mView
    }

    fun getLatestVersionSpotify() {
        if (NetworkChecker.isNetworkAvailable) {
            errorLayout(false)
            view.hideNoInternetLayout()
            loadData()
        } else {
            errorLayout(true)
            view.showNoInternetLayout()
        }
    }

    private fun loadData() {
        view.showProgress()
        job = CoroutineScope(Dispatchers.IO).launch {
            val result = spotifyInteractor.getSpotify()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        fullSpotifyModel = FullSpotifyModel(result.data)
                        updateUI()
                        view.hideProgress()
                        view.showLayoutCards()
                    }
                    is Result.Error -> {
                        errorLayout(true)
                        view.hideProgress()
                        view.showSnackbar(result.exception.message!!)
                    }
                }
            }
        }
    }

    fun downloadLatestVersion() {
        if (view.haveSaveFilePermission()) {
            fullSpotifyModel?.let {
                UtilsDownloadSpotify.downloadSpotify(it.latestLink)
                view.showSnackbar(StringService.getById(R.string.spotify_is_downloading))
            }
        } else {
            view.requestPermission()
        }
    }

    fun updateUI() {
        fullSpotifyModel?.let {
            val isSpotifyInstalled = UtilsSpotify.isSpotifyInstalled
            val installedVersion = UtilsSpotify.installedSpotifyVersion

            view.setLatestVersionAvailable(it.latestVersionName)
            if (isSpotifyInstalled && UtilsSpotify.isSpotifyUpdateAvailable(installedVersion,
                            it.latestVersionNumber)) {
                // Update Spotify
                view.showCardView()
                view.showFAB()
                view.setUpdateImageFAB()
                view.setInstalledVersion(installedVersion)
            } else if (!isSpotifyInstalled) {
                // Install spotify now
                view.showCardView()
                view.showFAB()
                view.setInstallImageFAB()
                view.setInstalledVersion(StringService.getById(R.string.spotify_not_installed))
            } else {
                //have latest version
                view.hideCardView()
                view.hideFAB()
                view.setInstalledVersion(StringService.getById(R.string.up_to_date))
            }
        }
    }

    fun startNotification() {
        if (queryPreferences.isEnableNotification) {
            PollService.setServiceAlarm(queryPreferences.isEnableNotification)
        } else {
            PollService.setServiceAlarm(false)
        }
    }

    private fun errorLayout(error: Boolean) {
        if (error) {
            view.hideLayoutCards()
            view.hideFAB()
        } else {
            view.showLayoutCards()
            view.hideFAB()
        }
    }

    fun onCreate() {
        getLatestVersionSpotify()
    }

    fun onDestroy() {
        job?.cancel()
    }
}
