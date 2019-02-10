package ru.ra66it.updaterforspotify.presentation.ui.customview

interface IRefreshStatus {

    fun reset()

    fun refreshing()

    fun refreshComplete()

    fun pullToRefresh()

    fun releaseToRefresh()

    fun pullProgress(pullDistance: Float, pullProgress: Float)
}