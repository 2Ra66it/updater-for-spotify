package ru.ra66it.updaterforspotify.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
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

        fab_orig.setOnClickListener { presenter.downloadLatestVersion() }

        presenter.showIntro()

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
    }

    public override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }


    override fun showProgress() {
        cardsContainer.visibility = GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun showErrorSnackbar(stringId: Int) {
        Snackbar.make(findViewById(android.R.id.content), getString(stringId), Snackbar.LENGTH_SHORT).show()
    }

    override fun showNoInternetLayout() {
        containerNoInternet.visibility = View.VISIBLE
    }

    override fun hideNoInternetLayout() {
        containerNoInternet.visibility = View.GONE
    }


    override fun hideCardView() {
        cardLatest.visibility = View.GONE
    }

    override fun showCardView() {
        cardLatest.visibility = View.VISIBLE
    }

    override fun hideFAB() {
        fab_orig.visibility = View.GONE
    }

    override fun setUpdateImageFAB() {
        fab_orig.setImageResource(R.drawable.ic_autorenew_black_24dp)
    }

    override fun setInstallImageFAB() {
        fab_orig.setImageResource(R.drawable.ic_file_download_black_24dp)
    }

    override fun showFAB() {
        fab_orig.visibility = View.VISIBLE
    }

    override fun setInstalledVersion(installedVersion: String) {
        tvInstalledVersion.text = installedVersion
    }

    override fun showLayoutCards() {
        cardsContainer.visibility = View.VISIBLE
    }

    override fun hideLayoutCards() {
        cardsContainer.visibility = View.GONE
    }


    override fun setLatestVersionAvailable(latestVersion: String) {
        tvLatestVersion.text = latestVersion
    }

    override fun showIntro() {
        startActivity(Intent(this, IntroActivity::class.java))
    }
}
