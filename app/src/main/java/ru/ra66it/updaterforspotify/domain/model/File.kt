package ru.ra66it.updaterforspotify.domain.model

import com.google.gson.annotations.SerializedName

data class File (
    @SerializedName("vername") var vername: String,
    @SerializedName("vercode") var vercode: Int,
    @SerializedName("md5sum") var md5sum: String,
    @SerializedName("filesize") var filesize: Int,
    @SerializedName("path") var path: String,
    @SerializedName("path_alt") var pathAlt: String
)