package com.lordinatec.battleship.gameplay.viewmodel

import com.lordinatec.battleship.gameplay.events.GameEvent
import com.lordinatec.battleship.gameplay.events.GameEventPublisher
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.Field
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.FieldState
import com.lordinatec.battleship.gameplay.model.RandomShipPlacer
import com.lordinatec.battleship.gameplay.model.Ship
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameControllerTest {

    @MockK
    private lateinit var friendlyField: Field

    @MockK
    private lateinit var enemyField: Field

    @MockK
    private lateinit var randomShipPlacerFactory: RandomShipPlacer.Factory

    @MockK
    private lateinit var shipPlacer: RandomShipPlacer

    @MockK
    private lateinit var gameEventPublisher: GameEventPublisher

    private val configuration = Configuration(10, 10)
    private val fieldState = MutableStateFlow(FieldState())

    private lateinit var gameController: GameController

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { friendlyField.fieldState } answers { fieldState }
        every { friendlyField.areAllShipsSunk() } answers { false }
        every { friendlyField.fieldIndexRange() } answers { 0 until configuration.columns * configuration.rows }
        every { friendlyField.placeShip(any(), any()) } just Runs
        every { enemyField.fieldState } answers { fieldState }
        every { enemyField.areAllShipsSunk() } answers { false }
        every { enemyField.fieldIndexRange() } answers { 0 until configuration.columns * configuration.rows }
        every { enemyField.placeShip(any(), any()) } just Runs
        every { randomShipPlacerFactory.create(any()) } returns shipPlacer
        every { shipPlacer.placeAllShips() } just Runs
        every { gameEventPublisher.publish(any()) } just Runs
        fieldState.update { FieldState() }
        gameController =
            GameController(friendlyField, enemyField, randomShipPlacerFactory, gameEventPublisher)
    }

    @Test
    fun testPlaceShipsAtRandom() = runTest {
        gameController.placeShipsAtRandom()
        verify { shipPlacer.placeAllShips() }
    }

    @Test
    fun testPlaceShipsAtRandomGameAlreadyStarted() = runTest {
        gameController.placeShipsAtRandom()
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertThrows(GameAlreadyStartedException::class.java) {
            gameController.placeShipsAtRandom()
        }
        verify(exactly = 1) { shipPlacer.placeAllShips() }
    }

    @Test
    fun testPlaceEnemyShipsAtRandom() = runTest {
        gameController.placeEnemyShipsAtRandom()
        verify { shipPlacer.placeAllShips() }
    }

    @Test
    fun testPlaceEnemyShipsAtRandomGameAlreadyStarted() = runTest {
        gameController.placeEnemyShipsAtRandom()
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertThrows(GameAlreadyStartedException::class.java) {
            gameController.placeEnemyShipsAtRandom()
        }
        verify(exactly = 1) { shipPlacer.placeAllShips() }
    }

    @Test
    fun testPlaceShip() = runTest {
        gameController.placeShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
        verify { friendlyField.placeShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5)) }
        gameController.placeShip(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
        verify { friendlyField.placeShip(Ship.BATTLESHIP, setOf(6, 7, 8, 9)) }
        gameController.placeShip(Ship.CRUISER, setOf(10, 11, 12))
        verify { friendlyField.placeShip(Ship.CRUISER, setOf(10, 11, 12)) }
        gameController.placeShip(Ship.SUBMARINE, setOf(13, 14, 15))
        verify { friendlyField.placeShip(Ship.SUBMARINE, setOf(13, 14, 15)) }
        gameController.placeShip(Ship.DESTROYER, setOf(16, 17))
        verify { friendlyField.placeShip(Ship.DESTROYER, setOf(16, 17)) }
    }

    @Test
    fun testPlaceShipGameAlreadyStarted() = runTest {
        gameController.placeShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
        gameController.placeShip(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
        gameController.placeShip(Ship.CRUISER, setOf(10, 11, 12))
        gameController.placeShip(Ship.SUBMARINE, setOf(13, 14, 15))
        gameController.placeShip(Ship.DESTROYER, setOf(16, 17))
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertThrows(GameAlreadyStartedException::class.java) {
            gameController.placeShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
        }
    }

    @Test
    fun testPlaceEnemyShip() = runTest {
        gameController.placeEnemyShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
        verify { enemyField.placeShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5)) }
        gameController.placeEnemyShip(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
        verify { enemyField.placeShip(Ship.BATTLESHIP, setOf(6, 7, 8, 9)) }
        gameController.placeEnemyShip(Ship.CRUISER, setOf(10, 11, 12))
        verify { enemyField.placeShip(Ship.CRUISER, setOf(10, 11, 12)) }
        gameController.placeEnemyShip(Ship.SUBMARINE, setOf(13, 14, 15))
        verify { enemyField.placeShip(Ship.SUBMARINE, setOf(13, 14, 15)) }
        gameController.placeEnemyShip(Ship.DESTROYER, setOf(16, 17))
        verify { enemyField.placeShip(Ship.DESTROYER, setOf(16, 17)) }
    }

    @Test
    fun testPlaceEnemyShipGameAlreadyStarted() = runTest {
        gameController.placeEnemyShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
        gameController.placeEnemyShip(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
        gameController.placeEnemyShip(Ship.CRUISER, setOf(10, 11, 12))
        gameController.placeEnemyShip(Ship.SUBMARINE, setOf(13, 14, 15))
        gameController.placeEnemyShip(Ship.DESTROYER, setOf(16, 17))
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertThrows(GameAlreadyStartedException::class.java) {
            gameController.placeEnemyShip(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
        }
    }

    @Test
    fun testFieldIndexRange() = runTest {
        gameController.fieldIndexRange()
        verify { friendlyField.fieldIndexRange() }
    }

    @Test
    fun testEnemyFieldIndexRange() = runTest {
        gameController.enemyFieldIndexRange()
        verify { enemyField.fieldIndexRange() }
    }

    @Test
    fun testStartGameReady() = runTest {
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        verify { gameEventPublisher.publish(GameEvent.GameCreated) }
    }

    @Test
    fun testStartGameAlreadyStarted() = runTest {
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertThrows(GameAlreadyStartedException::class.java) {
            gameController.startGame()
        }
    }

    @Test
    fun testStartGameShipsNotPlaced() = runTest {
        assertThrows(ShipsNotPlacedException::class.java) {
            gameController.startGame()
        }
    }

    @Test
    fun testEnemyShotReadyHit() = runTest {
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, null)
        }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, null)
        }
        val shootIndex = 1
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        assertFalse(gameController.turnState.value.isMyTurn)
        gameController.enemyShot(shootIndex)
        verify { friendlyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShotHit(shootIndex)) }
        assertTrue(gameController.turnState.value.isMyTurn)
    }

    @Test
    fun testEnemyShotReadyHitSunk() = runTest {
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        assertFalse(gameController.turnState.value.isMyTurn)
        gameController.enemyShot(shootIndex)
        verify { friendlyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShotHit(shootIndex)) }
        verify { gameEventPublisher.publish(GameEvent.ShipSunk(Ship.BATTLESHIP)) }
        assertTrue(gameController.turnState.value.isMyTurn)
    }

    @Test
    fun testEnemyShotReadyHitAllSunk() = runTest {
        var allShipsSunk = true
        every { friendlyField.areAllShipsSunk() } answers {
            allShipsSunk = !allShipsSunk
            allShipsSunk
        }
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        assertFalse(gameController.turnState.value.isMyTurn)
        gameController.enemyShot(shootIndex)
        verify { friendlyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShotHit(shootIndex)) }
        verify { gameEventPublisher.publish(GameEvent.ShipSunk(Ship.BATTLESHIP)) }
        verify { gameEventPublisher.publish(GameEvent.GameLost) }
        assertTrue(gameController.turnState.value.isMyTurn)
        assertTrue(gameController.turnState.value.isGameOver)
    }

    @Test
    fun testEnemyShotReadyMiss() = runTest {
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, null)
        }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, null)
        }
        val shootIndex = 18
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        assertFalse(gameController.turnState.value.isMyTurn)
        gameController.enemyShot(shootIndex)
        verify { friendlyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShotMissed(shootIndex)) }
        assertTrue(gameController.turnState.value.isMyTurn)
    }

    @Test
    fun testEnemyShotGameNotStarted() = runTest {
        assertThrows(GameNotActiveException::class.java) {
            gameController.enemyShot(0)
        }
    }

    @Test
    fun testEnemyShotGameOver() = runTest {
        var allShipsSunk = true
        every { friendlyField.areAllShipsSunk() } answers {
            allShipsSunk = !allShipsSunk
            allShipsSunk
        }
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        assertFalse(gameController.turnState.value.isMyTurn)
        gameController.enemyShot(shootIndex)
        verify { friendlyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShotHit(shootIndex)) }
        verify { gameEventPublisher.publish(GameEvent.ShipSunk(Ship.BATTLESHIP)) }
        verify { gameEventPublisher.publish(GameEvent.GameLost) }
        assertTrue(gameController.turnState.value.isMyTurn)
        assertTrue(gameController.turnState.value.isGameOver)
        assertThrows(GameNotActiveException::class.java) {
            gameController.enemyShot(0)
        }
    }

    @Test
    fun testEnemyShotWrongTurn() = runTest {
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        assertFalse(gameController.turnState.value.isMyTurn)
        gameController.enemyShot(shootIndex)
        assertTrue(gameController.turnState.value.isMyTurn)
        assertThrows(WrongTurnException::class.java) {
            gameController.enemyShot(0)
        }
    }

    @Test
    fun testEnemyShotAlreadyShot() = runTest {
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        gameController.shootAtEnemy(shootIndex)
        gameController.enemyShot(shootIndex)
        gameController.shootAtEnemy(shootIndex + 1)
        assertThrows(AlreadyShotException::class.java) {
            gameController.enemyShot(shootIndex)
        }
    }

    @Test
    fun testShootAtEnemyReadyHit() = runTest {
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, null)
        }
        val shootIndex = 1
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        verify { enemyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.MyShotHit(shootIndex)) }
        assertFalse(gameController.turnState.value.isMyTurn)
    }

    @Test
    fun testShootAtEnemyReadyHitSunk() = runTest {
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        verify { enemyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.MyShotHit(shootIndex)) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShipSunk(Ship.BATTLESHIP)) }
        assertFalse(gameController.turnState.value.isMyTurn)
    }

    @Test
    fun testShootAtEnemyReadyHitAllSunk() = runTest {
        every { enemyField.areAllShipsSunk() } answers { true }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        verify { enemyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.MyShotHit(shootIndex)) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShipSunk(Ship.BATTLESHIP)) }
        verify { gameEventPublisher.publish(GameEvent.GameWon) }
        assertFalse(gameController.turnState.value.isMyTurn)
        assertTrue(gameController.turnState.value.isGameOver)
    }

    @Test
    fun testShootAtEnemyReadyMiss() = runTest {
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, null)
        }
        val shootIndex = 18
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        verify { enemyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.MyShotMissed(shootIndex)) }
        assertFalse(gameController.turnState.value.isMyTurn)
    }

    @Test
    fun testShootAtEnemyGameNotStarted() = runTest {
        assertThrows(GameNotActiveException::class.java) {
            gameController.shootAtEnemy(0)
        }
    }

    @Test
    fun testShootAtEnemyGameOver() = runTest {
        every { enemyField.areAllShipsSunk() } answers { true }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        verify { enemyField.shoot(shootIndex) }
        verify { gameEventPublisher.publish(GameEvent.MyShotHit(shootIndex)) }
        verify { gameEventPublisher.publish(GameEvent.EnemyShipSunk(Ship.BATTLESHIP)) }
        verify { gameEventPublisher.publish(GameEvent.GameWon) }
        assertFalse(gameController.turnState.value.isMyTurn)
        assertTrue(gameController.turnState.value.isGameOver)
        assertThrows(GameNotActiveException::class.java) {
            gameController.shootAtEnemy(0)
        }
    }

    @Test
    fun testShootAtEnemyWrongTurn() = runTest {
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.turnState.value.isMyTurn)
        gameController.shootAtEnemy(shootIndex)
        assertFalse(gameController.turnState.value.isMyTurn)
        assertThrows(WrongTurnException::class.java) {
            gameController.shootAtEnemy(0)
        }
    }

    @Test
    fun testShootAtEnemyAlreadyShot() = runTest {
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        every { friendlyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        val shootIndex = 7
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        gameController.shootAtEnemy(shootIndex)
        gameController.enemyShot(shootIndex)
        assertThrows(AlreadyShotException::class.java) {
            gameController.shootAtEnemy(shootIndex)
        }
    }

    @Test
    fun testIsGameActiveBeforeCreate() = runTest {
        assertFalse(gameController.isGameActive())
    }

    @Test
    fun testIsGameActiveAfterCreate() = runTest {
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        assertTrue(gameController.isGameActive())
    }

    @Test
    fun testIsGameActiveAfterGameOver() = runTest {
        every { enemyField.areAllShipsSunk() } answers { true }
        every { enemyField.shoot(any()) } answers {
            val index = firstArg<FieldIndex>()
            Field.ShotResult(index <= 17, Ship.BATTLESHIP)
        }
        fieldState.update {
            it.copy(shipLocations = HashMap<Ship, Set<FieldIndex>>().apply {
                put(Ship.CARRIER, setOf(1, 2, 3, 4, 5))
                put(Ship.BATTLESHIP, setOf(6, 7, 8, 9))
                put(Ship.CRUISER, setOf(10, 11, 12))
                put(Ship.SUBMARINE, setOf(13, 14, 15))
                put(Ship.DESTROYER, setOf(16, 17))
            })
        }
        gameController.startGame()
        gameController.shootAtEnemy(0)
        assertFalse(gameController.isGameActive())
    }
}
