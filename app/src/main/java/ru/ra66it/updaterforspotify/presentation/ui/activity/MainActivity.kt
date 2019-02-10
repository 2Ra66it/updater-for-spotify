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
import ru.ra66it.updaterforspotify.domain.model.StatusState
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.viewmodel.SpotifyViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: SpotifyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdaterApp.applicationComponent.inject(this)
        setContentView(R.layout.activity_main)

        swipeLayout.setOnRefreshListener {
            viewModel.getLatestSpotify()
        }

        fab.setOnClickListener { downloadSpotify() }

        viewModel.spotifyLiveData.observe(this, Observer {
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
        })

        viewModel.getLatestSpotify()
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
        viewModel.updateUI()
        viewModel.startNotification()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    private fun showHaveLatestVersion() {
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
            viewModel.downloadSpotify()
            showSnackbar(getString(R.string.spotify_is_downloading))
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)
        }
    }

}
