package ru.ra66it.updaterforspotify.domain.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class File {

    @SerializedName("vername")
    @Expose
    var vername: String? = null
    @SerializedName("vercode")
    @Expose
    var vercode: Int = 0
    @SerializedName("md5sum")
    @Expose
    var md5sum: String? = null
    @SerializedName("filesize")
    @Expose
    var filesize: Int = 0
    @SerializedName("path")
    @Expose
    var path: String? = null
    @SerializedName("path_alt")
    @Expose
    var pathAlt: String? = null
}