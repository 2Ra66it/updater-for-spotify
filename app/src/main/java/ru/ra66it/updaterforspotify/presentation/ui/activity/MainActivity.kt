package ru.ra66it.updaterforspotify.presentation.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.presentation.mvp.presenter.SpotifyPresenter
import ru.ra66it.updaterforspotify.presentation.mvp.view.SpotifyView
import javax.inject.Inject

class MainActivity : AppCompatActivity(), SpotifyView {

    @Inject
    lateinit var presenter: SpotifyPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdaterApp.applicationComponent.inject(this)
        setContentView(R.layout.activity_main)

        presenter.setView(this)

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = false
            presenter.getLatestVersionSpotify()
        }

        fab.setOnClickListener { presenter.downloadLatestVersion() }

        presenter.onCreate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tool_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        presenter.updateUI()
        presenter.startNotification()
    }

    public override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }


    override fun showProgress() {
        setViewVisibility(cardsContainer, View.GONE)
        setViewVisibility(progressBar, View.VISIBLE)
    }

    override fun hideProgress() {
        setViewVisibility(progressBar, View.GONE)
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(fab, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showNoInternetLayout() {
        setViewVisibility(containerNoInternet, View.VISIBLE)
    }

    override fun hideNoInternetLayout() {
        setViewVisibility(containerNoInternet, View.GONE)
    }


    override fun hideCardView() {
        setViewVisibility(cardLatest, View.GONE)
    }

    override fun showCardView() {
        setViewVisibility(cardLatest, View.VISIBLE)
    }

    override fun hideFAB() {
        setViewVisibility(fab, View.GONE)
    }

    override fun setUpdateImageFAB() {
        fab.setImageResource(R.drawable.ic_autorenew_black_24dp)
    }

    override fun setInstallImageFAB() {
        fab.setImageResource(R.drawable.ic_file_download_black_24dp)
    }

    override fun showFAB() {
        setViewVisibility(fab, View.VISIBLE)
    }

    override fun setInstalledVersion(installedVersion: String) {
        tvInstalledVersion.text = installedVersion
    }

    override fun showLayoutCards() {
        setViewVisibility(cardsContainer, View.VISIBLE)
    }

    override fun hideLayoutCards() {
        setViewVisibility(cardsContainer, View.GONE)
    }

    private fun setViewVisibility(view: View, visibility: Int) {
        if (view.visibility != visibility) {
            view.visibility = visibility
        }
    }

    override fun setLatestVersionAvailable(latestVersion: String) {
        tvLatestVersion.text = latestVersion
    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1)
    }

    override fun haveSaveFilePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}
