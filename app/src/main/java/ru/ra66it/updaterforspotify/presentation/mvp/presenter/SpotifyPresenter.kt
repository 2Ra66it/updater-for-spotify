package ru.ra66it.updaterforspotify.presentation.mvp.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.models.FullSpotifyModel
import ru.ra66it.updaterforspotify.presentation.mvp.view.SpotifyView
import ru.ra66it.updaterforspotify.presentation.service.PollService
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify
import ru.ra66it.updaterforspotify.presentation.utils.UtilsSpotify

/**
 * Created by 2Rabbit on 11.11.2017.
 */

class SpotifyPresenter(private val spotifyInteractor: SpotifyInteractor, private val queryPreferences: QueryPreferences) {

    private lateinit var view: SpotifyView
    private var fullSpotifyModel: FullSpotifyModel? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun setView(mView: SpotifyView) {
        this.view = mView
    }

    fun getLatestVersionSpotify() {
        if (NetworkChecker.isNetworkAvailable) {
            loadData()
        } else {
            errorLayout(true)
            view.showNoInternetLayout()
        }
    }

    private fun loadData() {
        compositeDisposable.add(spotifyInteractor.latestSpotify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d ->
                    compositeDisposable.add(d)
                    errorLayout(false)
                    view.hideNoInternetLayout()
                    view.showProgress()
                }
                .doOnComplete {
                    updateUI()
                    view.hideProgress()
                    view.showLayoutCards()
                }
                .subscribe({ spotify -> fullSpotifyModel = FullSpotifyModel(spotify) }, {
                    errorLayout(true)
                    view.hideProgress()
                    view.showErrorSnackbar(R.string.error)
                }))
    }

    fun downloadLatestVersion() {
        UtilsDownloadSpotify.downloadSpotify(fullSpotifyModel!!.latestLink, fullSpotifyModel!!.latestVersionName)
    }

    fun updateUI() {
        fullSpotifyModel?.let {
            val isSpotifyInstalled = UtilsSpotify.isSpotifyInstalled
            val installedVersion = UtilsSpotify.installedSpotifyVersion

            view.setLatestVersionAvailable(fullSpotifyModel!!.latestVersionName)
            if (isSpotifyInstalled && UtilsSpotify.isSpotifyUpdateAvailable(installedVersion, fullSpotifyModel!!.latestVersionNumber)) {
                // Update Spotify
                view.showCardView()
                view.showFAB()
                view.setUpdateImageFAB()
                view.setInstalledVersion(installedVersion)
            } else if (!isSpotifyInstalled) {
                // Install spotify now
                view.setInstallImageFAB()
                view.showFAB()
                view.showCardView()
                view.setInstalledVersion(StringService.getById(R.string.spotify_not_installed))
            } else {
                //have latest version
                view.hideFAB()
                view.hideCardView()
                view.setInstalledVersion(StringService.getById(R.string.up_to_date))
            }
        }
    }

    private fun startNotification() {
        if (queryPreferences.isEnableNotification) {
            PollService.setServiceAlarm(queryPreferences.isEnableNotification)
        } else {
            PollService.setServiceAlarm(false)
        }
    }

    private fun errorLayout(bool: Boolean) {
        if (bool) {
            view.hideLayoutCards()
            view.hideFAB()
        } else {
            view.showLayoutCards()
            view.hideFAB()
        }
    }


    fun onCreate() {
        startNotification()
        getLatestVersionSpotify()
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

    fun showIntro() {
        if (queryPreferences.isFirstLaunch) {
            view.showIntro()
        }
    }
}
