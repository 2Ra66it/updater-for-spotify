package ru.ra66it.updaterforspotify.presentation.utils

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class VersionsComparator @Inject constructor() {

    fun compareVersion(installVersion: String, lastVersion: String): Int {
        val firstVersion = installVersion.split("\\.".toRegex())
        val secondVersion = lastVersion.split("\\.".toRegex())
        val length = max(firstVersion.size, secondVersion.size)

        for (i in 0 until length) {
            val firstPart = if (i < firstVersion.size) firstVersion[i].toInt() else 0
            val secondPart = if (i < secondVersion.size) secondVersion[i].toInt() else 0
            when {
                firstPart > secondPart -> return -1
                firstPart < secondPart -> return 1
            }
        }

        return 0
    }
}