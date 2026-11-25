package com.siti.cosmos.data.model

import com.google.gson.annotations.SerializedName

data class ApodResponse(
    @SerializedName("date")        val date: String        = "",
    @SerializedName("title")       val title: String       = "",
    @SerializedName("explanation") val explanation: String = "",
    @SerializedName("url")         val url: String         = "",
    @SerializedName("hdurl")       val hdUrl: String?      = null,
    @SerializedName("media_type")  val mediaType: String   = "image",
    @SerializedName("copyright")   val copyright: String?  = null,
    @SerializedName("service_version") val serviceVersion: String = ""
)
