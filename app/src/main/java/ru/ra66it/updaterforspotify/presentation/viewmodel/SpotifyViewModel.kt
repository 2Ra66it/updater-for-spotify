package ru.ra66it.updaterforspotify.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.ra66it.updaterforspotify.data.repositories.DownloadFileRepository
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.model.DownloadStatusState
import ru.ra66it.updaterforspotify.domain.model.Result
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class SpotifyViewModel @Inject constructor(
        private val spotifyInteractor: SpotifyInteractor,
        sharedPreferencesHelper: SharedPreferencesHelper,
        private val spotifyMapper: SpotifyMapper,
        private val downloadFileRepository: DownloadFileRepository,
        workersEnqueueManager: WorkersEnqueueManager
) : ViewModel(), CoroutineScope {

    private val job = Job()

    val spotifyLiveData: MutableLiveData<SpotifyStatusState> = MutableLiveData()
    val downloadFileLiveData: MutableLiveData<DownloadStatusState> = downloadFileRepository.downloadProgressLiveData

    init {
        if (sharedPreferencesHelper.isEnableNotification) {
            workersEnqueueManager.enqueuePeriodicCheckingIfDontExist(sharedPreferencesHelper.checkIntervalDay)
        }

        getLatestSpotify()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun getLatestSpotify() {
        spotifyLiveData.postValue(SpotifyStatusState.Loading)
        launch {
            when (val response = withContext(Dispatchers.IO) { spotifyInteractor.getSpotify() }) {
                is Result.Success -> {
                    val data = spotifyMapper.map(response.data)
                    data.isDownloading = downloadFileRepository.isDownloading
                    spotifyLiveData.postValue(SpotifyStatusState.Data(data))
                }
                is Result.Error -> {
                    spotifyLiveData.postValue(SpotifyStatusState.Error(response.exception))
                }
            }
        }
    }

    fun updateUI() {
        spotifyLiveData.value?.let {
            if (it is SpotifyStatusState.Data) {
                val value = it.spotify
                val data = spotifyMapper.updateInstalledVersion(value)
                spotifyLiveData.postValue(SpotifyStatusState.Data(data))
            }
        }
    }

    fun cancelDownloading() {
        downloadFileRepository.cancel()
    }

    fun downloadSpotify() {
        val value = spotifyLiveData.value
        if (value is SpotifyStatusState.Data) {
            val data = value.spotify
            downloadFileRepository.download(data.latestLink, data.latestVersionNumber)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}