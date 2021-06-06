package ru.ra66it.updaterforspotify

import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    @Test
    @Throws(Exception::class)
    fun testVersionCompare()  {
        assertEquals(-1, UtilsSpotify.compareVersion("8.5.99.614", "8.4.99.614"))
        assertEquals(0,  UtilsSpotify.compareVersion("8.4.99.614", "8.4.99.614"))
        assertEquals(1,  UtilsSpotify.compareVersion("8.4.99.614", "8.5.0.339"))
    }

}