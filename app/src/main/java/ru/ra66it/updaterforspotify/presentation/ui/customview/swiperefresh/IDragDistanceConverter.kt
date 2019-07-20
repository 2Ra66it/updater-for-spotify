package ru.ra66it.updaterforspotify.presentation.ui.customview.swiperefresh

interface IDragDistanceConverter {

    fun convert(scrollDistance: Float, refreshDistance: Float): Float
}