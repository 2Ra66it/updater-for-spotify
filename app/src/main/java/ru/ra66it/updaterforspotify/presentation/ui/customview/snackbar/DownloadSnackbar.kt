package ru.ra66it.updaterforspotify.presentation.ui.customview.snackbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.snackbar.BaseTransientBottomBar
import kotlinx.android.synthetic.main.snackbar_download_layout.view.*
import ru.ra66it.updaterforspotify.R

class DownloadSnackbar(parent: ViewGroup, @BaseTransientBottomBar.Duration duration: Int, snackbarCloseListener: () -> Unit) {

    private val snackbar: CustomSnackbar

    private var progress: ProgressBar? = null
    private var name: TextView? = null
    private var progressText: TextView? = null

    init {
        val inflater = LayoutInflater.from(parent.context)
        val content = inflater.inflate(R.layout.snackbar_download_layout, parent, false)
        snackbar = CustomSnackbar(parent, content)
        snackbar.view.setPadding(0, 0, 0, 0)
        snackbar.duration = duration

        progress = content.progress
        name = content.name
        progressText = content.progressText

        content.closeSnackBar.setOnClickListener {
            snackbar.dismiss()
            snackbarCloseListener.invoke()
        }
    }

    fun show(downloadName: String) {
        progress?.isIndeterminate = true
        name?.text = downloadName
        progress?.progress = 0
        progressText?.text = ""
        snackbar.show()
    }

    fun hide() {
        snackbar.dismiss()
    }

    fun setError(message: String) {
        progressText?.text = ""
        progress?.progress = 0
        name?.text = message
    }

    fun updateProgress(downloadProgress: Int) {
        progress?.let {
            it.isIndeterminate = false
            it.progress = downloadProgress
        }
        progressText?.let {
            val progressPercentage = "$downloadProgress%"
            it.text = progressPercentage
        }
    }

    fun isShown(): Boolean {
        return snackbar.isShown
    }

}

class CustomSnackbar(parent: ViewGroup, content: View) : BaseTransientBottomBar<CustomSnackbar>(parent, content, ContentViewCallback())

class ContentViewCallback : BaseTransientBottomBar.ContentViewCallback {

    override fun animateContentIn(delay: Int, duration: Int) {}

    override fun animateContentOut(delay: Int, duration: Int) {}
}
