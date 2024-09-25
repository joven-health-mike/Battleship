package com.lordinatec.battleship.gameplay.model

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RandomShipPlacerTest {

    @MockK
    private lateinit var field: Field

    private val configuration = Configuration(10, 10)
    private val fieldState = MutableStateFlow(FieldState())

    private lateinit var randomShipPlacer: RandomShipPlacer

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { field.configuration } answers { configuration }
        every { field.fieldIndexRange() } answers { 0 until configuration.columns * configuration.rows }
        every { field.fieldSize() } answers { configuration.columns * configuration.rows }
        every { field.fieldState } answers { fieldState }
        randomShipPlacer = RandomShipPlacer(field)
        fieldState.update { FieldState() }
    }

    @Test
    fun placeAllShipsTest() = runTest {
        every { field.isOccupied(any()) } answers {
            val index = firstArg<Int>()
            fieldState.value.shipLocations.values.any { it.contains(index) }
        }
        every { field.placeShip(any(), any()) } answers {
            val ship = firstArg<Ship>()
            val indices = secondArg<Set<Int>>()
            fieldState.update { fieldState.value.copy(shipLocations = fieldState.value.shipLocations + (ship to indices)) }
        }
        randomShipPlacer.placeAllShips()
        assertEquals(Ship.entries.size, field.fieldState.value.shipLocations.size)
    }

}
