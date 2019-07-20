package ru.ra66it.updaterforspotify.data.repositories

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import java.io.*
import java.lang.Exception
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class DownloadFileRepository @Inject constructor() : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private var job = Job()

    val downloadProgressLiveData: MutableLiveData<Int> = MutableLiveData()

    var path = ""

    fun download(stringUrl: String, version: String) {
        job = launch {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + UpdaterApp.instance.getString(R.string.spotify) + " $version.apk"
            val url = URL(stringUrl)
            val connection = url.openConnection()

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

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
                        downloadProgressLiveData.postValue(100)
                        break
                    }

                    outputStream.write(fileReader, 0, read)
                    current += read
                    downloadPart += read

                    if (downloadPart >= total / 100f) { //send progress every 1%
                        val percentage = (current * 100f) / total
                        downloadProgressLiveData.postValue(percentage.toInt())
                        downloadPart = 0
                    }

                }

                outputStream.flush()

            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                downloadProgressLiveData.postValue(-1)
                job.cancel()
            } finally {
                inputStream?.close()
                outputStream?.close()
            }

        }

    }

    fun cancel() {
        job.cancel()
    }

}