package ru.ra66it.updaterforspotify.domain.model

import com.google.gson.annotations.SerializedName

data class Data (
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
    @SerializedName("developer") val developer: Developer,
    @SerializedName("file") val file: File
)
