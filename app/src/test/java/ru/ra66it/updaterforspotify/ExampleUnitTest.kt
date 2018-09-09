package ru.ra66it.updaterforspotify

import org.junit.Assert.assertTrue
import org.junit.Test
import ru.ra66it.updaterforspotify.presentation.utils.UtilsSpotify.convertVersionToInt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    @Test()
    fun isSpotifyUpdateAvailableTest()  {
        val installedVersion = convertVersionToInt("8.4.70.654")
        val latestVersion = convertVersionToInt("Spotify Music 8.4.70.657")

        assertTrue(installedVersion < latestVersion)
    }

}