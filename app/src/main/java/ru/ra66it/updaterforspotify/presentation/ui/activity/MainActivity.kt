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
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.domain.model.StatusState
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.viewmodel.SpotifyViewModel
import ru.ra66it.updaterforspotify.presentation.ui.customview.snackbar.DownloadSnackbar
import ru.ra66it.updaterforspotify.presentation.ui.customview.swiperefresh.RefreshLayout
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var spotifyViewModel: SpotifyViewModel

    private val refreshListener = object : RefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            spotifyViewModel.getLatestSpotify()
        }
    }

    private val spotifyDataObserver = Observer<StatusState> {
        when (it) {
            is StatusState.Error -> {
                showError(it.exception.localizedMessage)
            }
            is StatusState.Loading -> {
                showLoading()
            }
            is StatusState.Data -> {
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

    private val downloadDataObserver = Observer<Triple<String, Int, Int>> {
        val name = it.first
        val state = it.second
        val progress = it.third
        when (state) {
            startDownload -> {
                snackbar?.show(name)
                fab.hide()
            }
            progressDownload -> {
                snackbar?.updateProgress(progress)
            }
            errorDownload -> {
                snackbar?.setError("Error")
            }
            else -> {
                snackbar?.hide()
                fab.show()
            }
        }
    }

    private var snackbar: DownloadSnackbar? = null

    private var snackbarCloseListener: () -> Unit = {
        spotifyViewModel.cancelDownloading()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UpdaterApp.applicationComponent.inject(this)

        swipeLayout.setOnRefreshListener(refreshListener)

        fab.setOnClickListener { downloadSpotify() }

        snackbar = DownloadSnackbar(container, Snackbar.LENGTH_INDEFINITE, snackbarCloseListener)

        spotifyViewModel.spotifyLiveData.observe(this, spotifyDataObserver)
        spotifyViewModel.downloadFileLiveData.observe(this, downloadDataObserver)
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
        spotifyViewModel.startNotification()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    private fun showHaveLatestVersion() {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.GONE
        fab.visibility = View.GONE
        tvInstalledVersion.text = StringService.getById(R.string.up_to_date)
    }

    private fun showHaveUpdate(installedVersion: String, latestVersionName: String) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
        fab.setImageResource(R.drawable.ic_autorenew_black_24dp)
        tvInstalledVersion.text = installedVersion
        tvLatestVersion.text = latestVersionName
    }

    private fun showInstallNow(installedVersion: String, latestVersionName: String) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
        fab.setImageResource(R.drawable.ic_file_download_black_24dp)
        tvInstalledVersion.text = installedVersion
        tvLatestVersion.text = latestVersionName
    }

    private fun showError(message: String) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.GONE
        fab.visibility = View.GONE
        showSnackbar(message)
    }

    private fun showLoading() {
        swipeLayout.setRefreshing(true)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(fab, message, Snackbar.LENGTH_LONG).show()
    }

    private fun downloadSpotify() {
        val havePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (havePermission) {
            downloadFile()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    saveFilePermissionCodeRequest)
        }
    }

    private fun downloadFile() {
        if (NetworkChecker.isNetworkAvailable) {
            spotifyViewModel.downloadSpotify()
        } else {
            showSnackbar(getString(R.string.no_internet_connection))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            saveFilePermissionCodeRequest -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadFile()
                }
                return
            }
            else -> {
            }
        }
    }
}
