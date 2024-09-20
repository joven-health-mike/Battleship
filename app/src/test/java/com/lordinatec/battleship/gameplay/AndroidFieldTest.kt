package com.lordinatec.battleship.gameplay

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AndroidFieldTest {

    private val configuration = Configuration(10, 10)

    private lateinit var androidField: AndroidField

    @BeforeTest
    fun setUp() {
        androidField = AndroidField(configuration)
        androidField.placeShip(Ship.CARRIER, (0..4).toSet())
        androidField.placeShip(
            Ship.BATTLESHIP,
            (configuration.columns..configuration.columns + 3).toSet()
        )
        androidField.placeShip(
            Ship.CRUISER,
            (configuration.columns * 2..configuration.columns * 2 + 2).toSet()
        )
        androidField.placeShip(
            Ship.SUBMARINE,
            (configuration.columns * 3..configuration.columns * 3 + 2).toSet()
        )
        androidField.placeShip(
            Ship.DESTROYER,
            (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
        )
    }

    @Test
    fun placeShipsTest() = runTest {
        androidField.reset(configuration)
        androidField.placeShip(Ship.CARRIER, (0..4).toSet())
        assertEquals(1, androidField.fieldState.value.shipLocations.size)

        androidField.placeShip(
            Ship.BATTLESHIP,
            (configuration.columns..configuration.columns + 3).toSet()
        )
        assertEquals(2, androidField.fieldState.value.shipLocations.size)

        androidField.placeShip(
            Ship.CRUISER,
            (configuration.columns * 2..configuration.columns * 2 + 2).toSet()
        )
        assertEquals(3, androidField.fieldState.value.shipLocations.size)

        androidField.placeShip(
            Ship.SUBMARINE,
            (configuration.columns * 3..configuration.columns * 3 + 2).toSet()
        )

        assertEquals(4, androidField.fieldState.value.shipLocations.size)

        androidField.placeShip(
            Ship.DESTROYER,
            (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
        )
        assertEquals(5, androidField.fieldState.value.shipLocations.size)

        assertThrows(IllegalArgumentException::class.java) {
            androidField.placeShip(
                Ship.DESTROYER,
                (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
            )
        }
    }

    @Test
    fun shootHitTest() = runTest {
        val index = 0
        val hit = androidField.shoot(index)
        assertTrue(hit)
        assertTrue(androidField.fieldState.value.hits.contains(index))
        assertFalse(androidField.fieldState.value.misses.contains(index))
    }

    @Test
    fun shootMissTest() = runTest {
        val index = configuration.columns - 1
        val hit = androidField.shoot(index)
        assertFalse(hit)
        assertFalse(androidField.fieldState.value.hits.contains(index))
        assertTrue(androidField.fieldState.value.misses.contains(index))
    }

    @Test
    fun sunkTest() = runTest {
        val carrier = Ship.CARRIER
        val carrierLocations = (0..4).toSet()
        assertTrue(androidField.fieldState.value.sunk.isEmpty())
        carrierLocations.forEach { androidField.shoot(it) }
        assertTrue(androidField.fieldState.value.hits.containsAll(carrierLocations))
        assertTrue(androidField.fieldState.value.sunk.contains(carrier))
    }
}