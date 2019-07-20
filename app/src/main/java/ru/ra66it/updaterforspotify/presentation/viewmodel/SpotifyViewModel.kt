package ru.ra66it.updaterforspotify.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.model.Result
import ru.ra66it.updaterforspotify.domain.model.StatusState
import ru.ra66it.updaterforspotify.presentation.service.PollService
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify
import ru.ra66it.updaterforspotify.data.repositories.DownloadFileRepository
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

    private val job = Job()

    val spotifyLiveData: MutableLiveData<StatusState> = MutableLiveData()
    val downloadFileLiveData: MutableLiveData<Triple<String, Int, Int>> = MutableLiveData()

    init {
        getLatestSpotify()

        downloadFileRepository.downloadProgressLiveData.observeForever {
            val data = (spotifyLiveData.value as StatusState.Data).spotify
            when (it) {
                100 -> {
                    downloadFileLiveData.postValue(Triple(data.latestVersionName, completeDownload, it))
                }
                -1 -> {
                    downloadFileLiveData.postValue(Triple(data.latestVersionName, errorDownload, it))
                }
                else -> {
                    downloadFileLiveData.postValue(Triple(data.latestVersionName, progressDownload, it))
                }
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun getLatestSpotify() {
        spotifyLiveData.postValue(StatusState.Loading)
        launch {
            val response = withContext(Dispatchers.IO) { spotifyInteractor.getSpotify() }
            when (response) {
                is Result.Success -> {
                    val data = spotifyMapper.map(response.data)
                    spotifyLiveData.postValue(StatusState.Data(data))
                }
                is Result.Error -> {
                    spotifyLiveData.postValue(StatusState.Error(response.exception))
                }
            }
        }
    }

    fun updateUI() {
        if (spotifyLiveData.value != null) {
            if (spotifyLiveData.value is StatusState.Data) {
                val value = (spotifyLiveData.value as StatusState.Data).spotify
                val data = spotifyMapper.updateInstalledVersion(value)
                spotifyLiveData.postValue(StatusState.Data(data))
            }
        }
    }

    fun cancelDownloading() {
        downloadFileRepository.cancel()
        downloadFileLiveData.postValue(Triple("", completeDownload, 0))
    }

    fun downloadSpotify() {
        val data = (spotifyLiveData.value as StatusState.Data).spotify
        downloadFileLiveData.postValue(Triple(data.latestVersionName, startDownload, 0))
        downloadFileRepository.download(data.latestLink, data.latestVersionNumber)
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