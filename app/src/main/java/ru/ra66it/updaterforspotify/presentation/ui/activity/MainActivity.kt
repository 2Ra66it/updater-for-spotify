package ru.ra66it.updaterforspotify.presentation.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.domain.model.DownloadStatusState
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.presentation.ui.customview.snackbar.DownloadSnackbar
import ru.ra66it.updaterforspotify.presentation.ui.customview.swiperefresh.RefreshLayout
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.viewmodel.SpotifyViewModel
import java.io.File
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
                        showInstallNow(data.installedVersion, data.latestVersionName, data.isDownloading)
                    }
                    spotifyHaveUpdate -> {
                        showHaveUpdate(data.installedVersion, data.latestVersionName, data.isDownloading)
                    }
                    spotifyIsLatest -> {
                        showHaveLatestVersion()
                    }
                }
            }
        }
    }

    private val downloadDataObserver = Observer<DownloadStatusState> {
        when (it) {
            is DownloadStatusState.Start -> {
                snackbar?.show(it.name)
                fab.hide()
            }
            is DownloadStatusState.Progress -> {
                snackbar?.let { snackBar ->
                    if (fab.isShown) {
                        fab.hide()
                    }
                    if (!snackBar.isShown()) {
                        snackBar.show(it.name)
                    }
                    snackBar.updateProgress(it.progress)
                }
            }
            is DownloadStatusState.Cancel -> {
                snackbar?.hide()
                fab.show()
            }
            is DownloadStatusState.Complete -> {
                snackbar?.hide()
                fab.show()

                openInstallApplicationActivity(it.path)
            }
            is DownloadStatusState.Error -> {
                it.exception.localizedMessage?.let {
                    snackbar?.setError(it)
                }
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

        snackbar = DownloadSnackbar(container, Snackbar.LENGTH_INDEFINITE, snackbarCloseListener)

        fab.hide()
        fab.setOnClickListener { downloadSpotify() }

        initObservers()
    }

    private fun openInstallApplicationActivity(path: String) {
        val file = File(path)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val install = Intent(Intent.ACTION_INSTALL_PACKAGE)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            install.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val uri = FileProvider.getUriForFile(this@MainActivity, this@MainActivity.packageName + ".provider", file)
            install.data = uri
            this@MainActivity.startActivity(install)
        } else {
            val install = Intent(Intent.ACTION_VIEW)
            val uri = Uri.fromFile(file)
            install.setDataAndType(uri, "application/vnd.android.package-archive")
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this@MainActivity.startActivity(install)
        }
    }

    private fun initObservers() {
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
    }

    private fun showHaveLatestVersion() {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.GONE
        fab.hide()
        tvInstalledVersion.text = StringService.getById(R.string.up_to_date)
    }

    private fun showHaveUpdate(installedVersion: String, latestVersionName: String, isDownloading: Boolean) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.VISIBLE
        tvInstalledVersion.text = installedVersion
        tvLatestVersion.text = latestVersionName
        if (!isDownloading) {
            fab.setImageResource(R.drawable.ic_autorenew_black_24dp)
            fab.show()
        }
    }

    private fun showInstallNow(installedVersion: String, latestVersionName: String, isDownloading: Boolean) {
        swipeLayout.setRefreshing(false)
        cardsContainer.visibility = View.VISIBLE
        cardLatest.visibility = View.VISIBLE
        tvInstalledVersion.text = installedVersion
        tvLatestVersion.text = latestVersionName
        if (!isDownloading) {
            fab.setImageResource(R.drawable.ic_file_download_black_24dp)
            fab.show()
        }
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
