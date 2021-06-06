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
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.databinding.ActivityMainBinding
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.viewmodel.UpdaterViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModel: UpdaterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UpdaterApp.applicationComponent.inject(this)

        with(binding) {
            swipeLayout.onRefreshListener = { viewModel.getLatestSpotify() }
            fab.hide()
            binding.fab.setOnClickListener { downloadSpotify() }
        }

        viewModel.liveData.observe(this, {
            when (it) {
                is SpotifyStatusState.Error -> {
                    showError(it.exception.localizedMessage ?: "")
                }
                is SpotifyStatusState.Loading -> {
                    showLoading()
                }
                is SpotifyStatusState.Data -> {
                    val data = it.spotify
                    when (data.spotifyState) {
                        spotifyNotInstalled -> showInstallNow(data)
                        spotifyHaveUpdate -> showHaveUpdate(data)
                        spotifyIsLatest -> showHaveLatestVersion()
                    }
                }
            }
        })
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
    }

    private fun showHaveLatestVersion() {
        with(binding) {
            swipeLayout.setRefreshing(false)
            cardsContainer.visibility = View.VISIBLE
            cardLatest.visibility = View.GONE
            fab.hide()
            tvInstalledVersion.text = StringService.getById(R.string.up_to_date)
        }
    }

    private fun showHaveUpdate(data: SpotifyData) {
        with(binding) {
            swipeLayout.setRefreshing(false)
            cardsContainer.visibility = View.VISIBLE
            cardLatest.visibility = View.VISIBLE
            tvInstalledVersion.text = data.installedVersion
            tvLatestVersion.text = data.latestVersionName
            fab.setImageResource(R.drawable.ic_autorenew_black_24dp)
            fab.show()
        }
    }

    private fun showInstallNow(data: SpotifyData) {
        with(binding) {
            swipeLayout.setRefreshing(false)
            cardsContainer.visibility = View.VISIBLE
            cardLatest.visibility = View.VISIBLE
            tvInstalledVersion.text = data.installedVersion
            tvLatestVersion.text = data.latestVersionName
            fab.setImageResource(R.drawable.ic_file_download_black_24dp)
            fab.show()
        }
    }

    private fun showError(message: String) {
        with(binding) {
            swipeLayout.setRefreshing(false)
            cardsContainer.visibility = View.GONE
            fab.hide()
        }
        showSnackbar(message)
    }

    private fun showLoading() {
        binding.swipeLayout.setRefreshing(true)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.fab, message, Snackbar.LENGTH_LONG).show()
    }

    private fun downloadSpotify() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            downloadFile()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                saveFilePermissionCodeRequest
            )
        }
    }

    private fun downloadFile() {
        val messageId = if (NetworkChecker.isNetworkAvailable) {
            viewModel.downloadSpotify()
            R.string.downloading
        } else {
            R.string.no_internet_connection
        }
        showSnackbar(getString(messageId))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
