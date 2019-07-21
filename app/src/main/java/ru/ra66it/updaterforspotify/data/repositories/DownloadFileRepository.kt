package ru.ra66it.updaterforspotify.data.repositories

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.domain.model.DownloadStatusState
import java.io.*
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class DownloadFileRepository @Inject constructor() : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private var job = Job()
    val downloadProgressLiveData: MutableLiveData<DownloadStatusState> = MutableLiveData()
    var isDownloading: Boolean = false


    fun download(stringUrl: String, version: String) {
        job = launch {
            val name = UpdaterApp.instance.getString(R.string.spotify) + " $version.apk"
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + name
            val url = URL(stringUrl)
            val connection = url.openConnection()

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            isDownloading = true
            downloadProgressLiveData.postValue(DownloadStatusState.Start(name))

            try {
                var current = 0
                var downloadPart = 0
                val fileReader = ByteArray(1024 * 4)

                outputStream = FileOutputStream(path)

                val total = connection.contentLength

                inputStream = BufferedInputStream(connection.getInputStream())

                while (job.isActive) {
                    val read = inputStream.read(fileReader)

                    if (read == -1) {
                        isDownloading = false
                        downloadProgressLiveData.postValue(DownloadStatusState.Complete(path))
                        break
                    }

                    outputStream.write(fileReader, 0, read)
                    current += read
                    downloadPart += read

                    if (downloadPart >= total / 100f) { //send progress every 1%
                        val percentage = (current * 100f) / total
                        downloadProgressLiveData.postValue(DownloadStatusState.Progress(percentage.toInt(), name))
                        downloadPart = 0
                    }
                }

                outputStream.flush()

            } catch (e: Exception) {
                isDownloading = false
                job.cancel()
                downloadProgressLiveData.postValue(DownloadStatusState.Error(e))
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
    }

    fun cancel() {
        isDownloading = false
        job.cancel()
        downloadProgressLiveData.postValue(DownloadStatusState.Cancel)
    }

}