package ru.ra66it.updaterforspotify.domain.model

import com.google.gson.annotations.SerializedName

data class Spotify(@SerializedName("data") val data: Data)

data class Data(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("package") val _package: String,
    @SerializedName("uname") val uname: String,
    @SerializedName("size") val size: Int,
    @SerializedName("icon") val icon: String,
    @SerializedName("graphic") val graphic: String,
    @SerializedName("added") val added: String,
    @SerializedName("modified") val modified: String,
    @SerializedName("updated") val updated: String,
    @SerializedName("main_package") val mainPackage: Any,
    @SerializedName("file") val file: File
)


data class File(
    @SerializedName("vername") val vername: String,
    @SerializedName("vercode") val vercode: Int,
    @SerializedName("md5sum") val md5sum: String,
    @SerializedName("filesize") val filesize: Int,
    @SerializedName("path") val path: String,
    @SerializedName("path_alt") val pathAlt: String
)