package ru.ra66it.updaterforspotify.presentation.ui.customview

class MaterialDragDistanceConverter : IDragDistanceConverter {

    override fun convert(scrollDistance: Float, refreshDistance: Float): Float {
        val originalDragPercent = scrollDistance / refreshDistance
        val dragPercent = Math.min(1.0f, Math.abs(originalDragPercent))
        val extraOS = Math.abs(scrollDistance) - refreshDistance
        val tensionSlingshotPercent = Math.max(0f,
                Math.min(extraOS, refreshDistance * 2.0f) / refreshDistance)
        val tensionPercent = (tensionSlingshotPercent / 4 -
                Math.pow((tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = refreshDistance * tensionPercent * 2f

        val convertY = (refreshDistance * dragPercent + extraMove).toInt()

        return convertY.toFloat()
    }
}