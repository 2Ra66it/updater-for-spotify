package ru.ra66it.updaterforspotify.presentation.mvp.view


/**
 * Created by 2Rabbit on 06.11.2017.
 */

interface SpotifyView {

    fun showProgress()

    fun hideProgress()

    fun showErrorSnackbar(message: String)

    fun showNoInternetLayout()

    fun hideNoInternetLayout()

    fun hideCardView()

    fun showCardView()

    fun hideFAB()

    fun setUpdateImageFAB()

    fun setInstallImageFAB()

    fun showFAB()

    fun setInstalledVersion(installedVersion: String)

    fun showLayoutCards()

    fun hideLayoutCards()

    fun setLatestVersionAvailable(latestVersion: String)

    fun requestPermission()

    fun haveSaveFilePermission(): Boolean
}
