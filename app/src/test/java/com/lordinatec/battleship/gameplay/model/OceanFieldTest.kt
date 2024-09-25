package com.lordinatec.battleship.gameplay.model

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
class OceanFieldTest {

    private val configuration = Configuration(10, 10)

    private lateinit var oceanField: OceanField

    @BeforeTest
    fun setUp() {
        oceanField = OceanField(configuration)
        oceanField.placeShip(Ship.CARRIER, (0..4).toSet())
        oceanField.placeShip(
            Ship.BATTLESHIP,
            (configuration.columns..configuration.columns + 3).toSet()
        )
        oceanField.placeShip(
            Ship.CRUISER,
            (configuration.columns * 2..configuration.columns * 2 + 2).toSet()
        )
        oceanField.placeShip(
            Ship.SUBMARINE,
            (configuration.columns * 3..configuration.columns * 3 + 2).toSet()
        )
        oceanField.placeShip(
            Ship.DESTROYER,
            (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
        )
    }

    @Test
    fun placeShipsTest() = runTest {
        oceanField = OceanField(configuration)
        oceanField.placeShip(Ship.CARRIER, (0..4).toSet())
        assertEquals(1, oceanField.fieldState.value.shipLocations.size)

        oceanField.placeShip(
            Ship.BATTLESHIP,
            (configuration.columns..configuration.columns + 3).toSet()
        )
        assertEquals(2, oceanField.fieldState.value.shipLocations.size)

        oceanField.placeShip(
            Ship.CRUISER,
            (configuration.columns * 2..configuration.columns * 2 + 2).toSet()
        )
        assertEquals(3, oceanField.fieldState.value.shipLocations.size)

        oceanField.placeShip(
            Ship.SUBMARINE,
            (configuration.columns * 3..configuration.columns * 3 + 2).toSet()
        )

        assertEquals(4, oceanField.fieldState.value.shipLocations.size)

        oceanField.placeShip(
            Ship.DESTROYER,
            (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
        )
        assertEquals(5, oceanField.fieldState.value.shipLocations.size)

        assertThrows(IllegalArgumentException::class.java) {
            oceanField.placeShip(
                Ship.DESTROYER,
                (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
            )
        }
    }

    @Test
    fun shootHitTest() = runTest {
        val index = 0
        val result = oceanField.shoot(index)
        assertTrue(result.hit)
        assertTrue(oceanField.fieldState.value.hits.contains(index))
        assertFalse(oceanField.fieldState.value.misses.contains(index))
    }

    @Test
    fun shootMissTest() = runTest {
        val index = configuration.columns - 1
        val result = oceanField.shoot(index)
        assertFalse(result.hit)
        assertFalse(oceanField.fieldState.value.hits.contains(index))
        assertTrue(oceanField.fieldState.value.misses.contains(index))
    }

    @Test
    fun sunkTest() = runTest {
        val carrier = Ship.CARRIER
        val carrierLocations = (0..4).toSet()
        assertTrue(oceanField.fieldState.value.sunk.isEmpty())
        carrierLocations.forEach { oceanField.shoot(it) }
        assertTrue(oceanField.fieldState.value.hits.containsAll(carrierLocations))
        assertTrue(oceanField.fieldState.value.sunk.contains(carrier))
    }
}
