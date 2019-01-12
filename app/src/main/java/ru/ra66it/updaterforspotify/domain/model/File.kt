package ru.ra66it.updaterforspotify.domain.model

import com.google.gson.annotations.SerializedName

data class File (
    @SerializedName("vername") val vername: String,
    @SerializedName("vercode") val vercode: Int,
    @SerializedName("md5sum") val md5sum: String,
    @SerializedName("filesize") val filesize: Int,
    @SerializedName("path") val path: String,
    @SerializedName("path_alt") val pathAlt: String
)