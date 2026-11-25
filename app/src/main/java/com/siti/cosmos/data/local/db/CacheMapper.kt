package com.siti.cosmos.data.local.db

import com.siti.cosmos.data.model.*

// ── APOD ──────────────────────────────────────────────────────────────────────

fun ApodResponse.toEntity(): ApodCacheEntity = ApodCacheEntity(
    date           = date,
    title          = title,
    explanation    = explanation,
    url            = url,
    hdUrl          = hdUrl,
    mediaType      = mediaType,
    copyright      = copyright,
    serviceVersion = serviceVersion
)

fun ApodCacheEntity.toModel(): ApodResponse = ApodResponse(
    date           = date,
    title          = title,
    explanation    = explanation,
    url            = url,
    hdUrl          = hdUrl,
    mediaType      = mediaType,
    copyright      = copyright,
    serviceVersion = serviceVersion
)

// ── Mars ──────────────────────────────────────────────────────────────────────

fun MarsPhoto.toEntity(rover: String, page: Int): MarsCacheEntity = MarsCacheEntity(
    id              = id,
    sol             = sol,
    imgSrc          = imgSrc,
    earthDate       = earthDate,
    cameraName      = camera.name,
    cameraFullName  = camera.fullName,
    roverName       = this.rover.name,
    roverStatus     = this.rover.status,
    roverLaunchDate = this.rover.launchDate,
    roverLandingDate = this.rover.landingDate,
    rover           = rover,
    page            = page
)

fun MarsCacheEntity.toModel(): MarsPhoto = MarsPhoto(
    id        = id,
    sol       = sol,
    imgSrc    = imgSrc,
    earthDate = earthDate,
    camera    = MarsCamera(name = cameraName, fullName = cameraFullName),
    rover     = MarsRover(
        name        = roverName,
        status      = roverStatus,
        launchDate  = roverLaunchDate,
        landingDate = roverLandingDate
    )
)

// ── NEO ───────────────────────────────────────────────────────────────────────

fun NeoObject.toEntity(startDate: String, endDate: String): NeoCacheEntity {
    val approach = closeApproachData.firstOrNull()
    return NeoCacheEntity(
        id                     = id,
        name                   = name,
        nasaJplUrl             = nasaJplUrl,
        absoluteMagnitude      = absoluteMagnitude,
        isPotentiallyHazardous = isPotentiallyHazardous,
        diameterMinKm          = estimatedDiameter.kilometers.min,
        diameterMaxKm          = estimatedDiameter.kilometers.max,
        closeApproachDate      = approach?.closeApproachDate      ?: "",
        relativeVelocityKmh    = approach?.relativeVelocity?.kmPerHour ?: "0",
        missDistanceKm         = approach?.missDistance?.kilometers ?: "0",
        missDistanceLunar      = approach?.missDistance?.lunar      ?: "0",
        orbitingBody           = approach?.orbitingBody             ?: "",
        startDate              = startDate,
        endDate                = endDate
    )
}

fun NeoCacheEntity.toModel(): NeoObject = NeoObject(
    id                     = id,
    name                   = name,
    nasaJplUrl             = nasaJplUrl,
    absoluteMagnitude      = absoluteMagnitude,
    isPotentiallyHazardous = isPotentiallyHazardous,
    estimatedDiameter      = EstimatedDiameter(
        kilometers = DiameterRange(min = diameterMinKm, max = diameterMaxKm)
    ),
    closeApproachData      = listOf(
        CloseApproachData(
            closeApproachDate = closeApproachDate,
            relativeVelocity  = Velocity(kmPerHour = relativeVelocityKmh),
            missDistance      = MissDistance(kilometers = missDistanceKm, lunar = missDistanceLunar),
            orbitingBody      = orbitingBody
        )
    )
)
