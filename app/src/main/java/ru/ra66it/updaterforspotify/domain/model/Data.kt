package ru.ra66it.updaterforspotify.domain.model

import com.google.gson.annotations.SerializedName

data class Data (
    @SerializedName("id") val id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("package") var _package: String,
    @SerializedName("uname") var uname: String,
    @SerializedName("size") var size: Int,
    @SerializedName("icon") var icon: String,
    @SerializedName("graphic") var graphic: String,
    @SerializedName("added") var added: String,
    @SerializedName("modified") var modified: String,
    @SerializedName("updated") var updated: String,
    @SerializedName("main_package") var mainPackage: Any,
    @SerializedName("developer") var developer: Developer,
    @SerializedName("file") var file: File
)
