package com.siti.cosmos.data.model

import com.google.gson.annotations.SerializedName

data class NeoFeedResponse(
    @SerializedName("element_count")          val elementCount: Int = 0,
    @SerializedName("near_earth_objects")     val nearEarthObjects: Map<String, List<NeoObject>> = emptyMap()
)

data class NeoObject(
    @SerializedName("id")                                val id: String                        = "",
    @SerializedName("name")                              val name: String                      = "",
    @SerializedName("nasa_jpl_url")                      val nasaJplUrl: String                = "",
    @SerializedName("absolute_magnitude_h")              val absoluteMagnitude: Double         = 0.0,
    @SerializedName("is_potentially_hazardous_asteroid") val isPotentiallyHazardous: Boolean   = false,
    @SerializedName("estimated_diameter")                val estimatedDiameter: EstimatedDiameter = EstimatedDiameter(),
    @SerializedName("close_approach_data")               val closeApproachData: List<CloseApproachData> = emptyList()
)

data class EstimatedDiameter(
    @SerializedName("kilometers") val kilometers: DiameterRange = DiameterRange()
)

data class DiameterRange(
    @SerializedName("estimated_diameter_min") val min: Double = 0.0,
    @SerializedName("estimated_diameter_max") val max: Double = 0.0
)

data class CloseApproachData(
    @SerializedName("close_approach_date")       val closeApproachDate: String     = "",
    @SerializedName("relative_velocity")         val relativeVelocity: Velocity    = Velocity(),
    @SerializedName("miss_distance")             val missDistance: MissDistance    = MissDistance(),
    @SerializedName("orbiting_body")             val orbitingBody: String          = ""
)

data class Velocity(
    @SerializedName("kilometers_per_hour") val kmPerHour: String = "0"
)

data class MissDistance(
    @SerializedName("kilometers") val kilometers: String = "0",
    @SerializedName("lunar")      val lunar: String      = "0"
)
