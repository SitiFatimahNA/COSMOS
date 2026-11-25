package com.siti.cosmos.ui.navigation

object NavRoutes {
    const val SPLASH       = "splash"
    const val HOME         = "home"
    const val APOD         = "apod"
    const val MARS         = "mars"
    const val NEO          = "neo"
    const val FAVORITES    = "favorites"
    const val SETTINGS     = "settings"
    const val PLANETS      = "planets"

    // Routes with args
    const val PLANET_DETAIL = "planet_detail/{planetName}"
    const val MARS_DETAIL   = "mars_detail/{photoId}"
    const val NEO_DETAIL    = "neo_detail/{neoId}"

    fun planetDetail(name: String) = "planet_detail/$name"
    fun marsDetail(photoId: Int)   = "mars_detail/$photoId"
    fun neoDetail(neoId: String)   = "neo_detail/$neoId"
}
