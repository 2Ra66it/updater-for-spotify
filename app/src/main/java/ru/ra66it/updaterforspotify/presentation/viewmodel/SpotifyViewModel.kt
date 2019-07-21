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
import ru.ra66it.updaterforspotify.presentation.service.PollService
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class SpotifyViewModel @Inject constructor(
        private val spotifyInteractor: SpotifyInteractor,
        private val sharedPreferencesHelper: SharedPreferencesHelper,
        private val spotifyMapper: SpotifyMapper,
        private val downloadFileRepository: DownloadFileRepository
) : ViewModel(), CoroutineScope {

    val job = Job()

    val spotifyLiveData: MutableLiveData<SpotifyStatusState> = MutableLiveData()
    val downloadFileLiveData: MutableLiveData<DownloadStatusState> = downloadFileRepository.downloadProgressLiveData

    init {
        getLatestSpotify()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun getLatestSpotify() {
        spotifyLiveData.postValue(SpotifyStatusState.Loading)
        launch {
            val response = withContext(Dispatchers.IO) { spotifyInteractor.getSpotify() }
            when (response) {
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
        if (spotifyLiveData.value != null) {
            if (spotifyLiveData.value is SpotifyStatusState.Data) {
                val value = (spotifyLiveData.value as SpotifyStatusState.Data).spotify
                val data = spotifyMapper.updateInstalledVersion(value)
                spotifyLiveData.postValue(SpotifyStatusState.Data(data))
            }
        }
    }

    fun cancelDownloading() {
        downloadFileRepository.cancel()
    }

    fun downloadSpotify() {
        if (spotifyLiveData.value is SpotifyStatusState.Data) {
            val data = (spotifyLiveData.value as SpotifyStatusState.Data).spotify
            downloadFileRepository.download(data.latestLink, data.latestVersionNumber)
        }
        // UtilsDownloadSpotify.downloadSpotify(data.latestLink, data.latestVersionNumber)
    }

    fun startNotification() {
        PollService.setServiceAlarm(sharedPreferencesHelper.isEnableNotification,
                sharedPreferencesHelper.checkIntervalDay)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}