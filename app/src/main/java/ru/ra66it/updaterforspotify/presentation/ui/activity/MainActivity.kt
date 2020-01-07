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
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.presentation.ui.customview.swiperefresh.RefreshLayout
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.utils.UtilsSpotify
import ru.ra66it.updaterforspotify.presentation.viewmodel.SpotifyViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var spotifyViewModel: SpotifyViewModel

    private val refreshListener = object : RefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            spotifyViewModel.getLatestSpotify()
        }
    }

    private val spotifyDataObserver = Observer<SpotifyStatusState> {
        when (it) {
            is SpotifyStatusState.Error -> {
                it.exception.localizedMessage?.let {
                    showError(it)
                }
            }
            is SpotifyStatusState.Loading -> {
                showLoading()
            }
            is SpotifyStatusState.Data -> {
                val data = it.spotify
                when (data.spotifyState) {
                    spotifyNotInstalled -> {
                        showInstallNow(data.installedVersion, data.latestVersionName)
                    }
                    spotifyHaveUpdate -> {
                        showHaveUpdate(data.installedVersion, data.latestVersionName)
                    }
                    spotifyIsLatest -> {
                        showHaveLatestVersion()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UpdaterApp.applicationComponent.inject(this)

        swipeLayout.setOnRefreshListener(refreshListener)

        fab.hide()
        fab.setOnClickListener { downloadSpotify() }

        initObservers()
    }

    private fun initObservers() {
        spotifyViewModel.spotifyLiveData.observe(this, spotifyDataObserver)
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
        spotifyViewModel.updateUI()
    }

    private fun showHaveLatestVersion() {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.GONE
        fab.hide()
        tvInstalledVersion.text = StringService.getById(R.string.up_to_date)
    }

    private fun showHaveUpdate(installedVersion: String, latestVersionName: String) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.VISIBLE
        tvInstalledVersion.text = installedVersion
        tvLatestVersion.text = latestVersionName
        fab.setImageResource(R.drawable.ic_autorenew_black_24dp)
        fab.show()
    }

    private fun showInstallNow(installedVersion: String, latestVersionName: String) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.VISIBLE
        tvInstalledVersion.text = installedVersion
        tvLatestVersion.text = latestVersionName
        fab.setImageResource(R.drawable.ic_file_download_black_24dp)
        fab.show()
    }

    private fun showError(message: String) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.GONE
        fab.hide()
        showSnackbar(message)
    }

    private fun showLoading() {
        swipeLayout.setRefreshing(true)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(fab, message, Snackbar.LENGTH_LONG).show()
    }

    private fun downloadSpotify() {
        if (UtilsSpotify.haveStoragePermission(this)) {
            downloadFile()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    saveFilePermissionCodeRequest)
        }
    }

    private fun downloadFile() {
        val messageId = if (NetworkChecker.isNetworkAvailable) {
            spotifyViewModel.downloadSpotify()
            R.string.downloading
        } else {
            R.string.no_internet_connection
        }
        showSnackbar(getString(messageId))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            saveFilePermissionCodeRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile()
                }
                return
            }
            else -> {
            }
        }
    }
}
