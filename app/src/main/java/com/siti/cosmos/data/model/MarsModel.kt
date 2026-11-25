package com.siti.cosmos.data.model

import com.google.gson.annotations.SerializedName

data class MarsPhotosResponse(
    @SerializedName("photos") val photos: List<MarsPhoto> = emptyList()
)

data class MarsPhoto(
    @SerializedName("id")          val id: Int          = 0,
    @SerializedName("sol")         val sol: Int         = 0,
    @SerializedName("img_src")     val imgSrc: String   = "",
    @SerializedName("earth_date")  val earthDate: String = "",
    @SerializedName("camera")      val camera: MarsCamera = MarsCamera(),
    @SerializedName("rover")       val rover: MarsRover   = MarsRover()
)

data class MarsCamera(
    @SerializedName("id")        val id: Int        = 0,
    @SerializedName("name")      val name: String   = "",
    @SerializedName("full_name") val fullName: String = ""
)

data class MarsRover(
    @SerializedName("id")          val id: Int          = 0,
    @SerializedName("name")        val name: String     = "",
    @SerializedName("status")      val status: String   = "",
    @SerializedName("launch_date") val launchDate: String = "",
    @SerializedName("landing_date")val landingDate: String = ""
)
