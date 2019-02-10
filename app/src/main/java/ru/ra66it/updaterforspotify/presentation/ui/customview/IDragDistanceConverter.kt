package ru.ra66it.updaterforspotify.presentation.ui.customview

interface IDragDistanceConverter {

    fun convert(scrollDistance: Float, refreshDistance: Float): Float
}