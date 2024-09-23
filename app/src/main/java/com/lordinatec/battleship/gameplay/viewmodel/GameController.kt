package com.lordinatec.battleship.gameplay.viewmodel

import com.lordinatec.battleship.gameplay.events.GameEvent
import com.lordinatec.battleship.gameplay.events.GameEventPublisher
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.Field
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.RandomShipPlacer
import com.lordinatec.battleship.gameplay.model.Ship
import com.lordinatec.battleship.gameplay.model.TurnState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GameController(
    val myField: Field,
    val enemyField: Field,
    private val shipPlacerFactory: RandomShipPlacer.Factory,
    private val gameEventPublisher: GameEventPublisher,
) {
    private val _turnState = MutableStateFlow(TurnState())
    val turnState = _turnState.asStateFlow()

    private var gameStarted = false
    private var gameEnded = false
    var shots = HashSet<FieldIndex>()
    var enemyShots = HashSet<FieldIndex>()

    fun placeShipsAtRandom() {
        if (gameStarted) throw GameAlreadyStartedException()
        shipPlacerFactory.create(myField).placeAllShips()
    }

    fun placeEnemyShipsAtRandom() {
        if (gameStarted) throw GameAlreadyStartedException()
        shipPlacerFactory.create(enemyField).placeAllShips()
    }

    fun placeShip(ship: Ship, locations: Set<FieldIndex>) {
        if (gameStarted) throw GameAlreadyStartedException()
        myField.placeShip(ship, locations)
    }

    fun placeEnemyShip(ship: Ship, locations: Set<FieldIndex>) {
        if (gameStarted) throw GameAlreadyStartedException()
        enemyField.placeShip(ship, locations)
    }

    fun fieldIndexRange() = myField.fieldIndexRange()

    fun enemyFieldIndexRange() = enemyField.fieldIndexRange()

    fun startGame() {
        if (gameStarted) throw GameAlreadyStartedException()
        if (myField.fieldState.value.shipLocations.size != Ship.entries.size
            || enemyField.fieldState.value.shipLocations.size != Ship.entries.size
        ) throw ShipsNotPlacedException()
        gameStarted = true
        gameEnded = false
        gameEventPublisher.publish(GameEvent.GameCreated)
    }

    fun shootAtEnemy(location: FieldIndex) {
        if (!isGameActive()) throw GameNotActiveException()
        if (!turnState.value.isMyTurn) throw WrongTurnException()
        if (shots.contains(location)) throw AlreadyShotException()
        shots.add(location)
        val shotResult = enemyField.shoot(location)
        if (shotResult.hit) {
            gameEventPublisher.publish(GameEvent.MyShotHit(location))
        } else {
            gameEventPublisher.publish(GameEvent.MyShotMissed(location))
        }
        if (shotResult.sunk != null) {
            gameEventPublisher.publish(GameEvent.EnemyShipSunk(shotResult.sunk))
        }
        maybeEndGame()
        _turnState.update { it.copy(isMyTurn = false) }
    }

    fun enemyShot(location: FieldIndex) {
        if (!isGameActive()) throw GameNotActiveException()
        if (turnState.value.isMyTurn) throw WrongTurnException()
        if (enemyShots.contains(location)) throw AlreadyShotException()
        enemyShots.add(location)
        val shotResult = myField.shoot(location)
        if (shotResult.hit) {
            gameEventPublisher.publish(GameEvent.EnemyShotHit(location))
        } else {
            gameEventPublisher.publish(GameEvent.EnemyShotMissed(location))
        }
        if (shotResult.sunk != null) {
            gameEventPublisher.publish(GameEvent.ShipSunk(shotResult.sunk))
        }
        maybeEndGame()
        _turnState.update { it.copy(isMyTurn = true) }
    }

    fun isGameActive() = gameStarted && !gameEnded

    private fun maybeEndGame() {
        if (myField.areAllShipsSunk()) {
            _turnState.update {
                it.copy(isGameOver = true)
            }
            gameEnded = true
            gameEventPublisher.publish(GameEvent.GameLost)
        } else if (enemyField.areAllShipsSunk()) {
            _turnState.update {
                it.copy(isGameOver = true)
            }
            gameEnded = true
            gameEventPublisher.publish(GameEvent.GameWon)
        }
    }

    /**
     * Factory for creating game controllers.
     */
    fun interface Factory {
        /**
         * Creates a new game controller.
         *
         * @return The new game controller.
         */
        fun create(): GameController
    }

    class FactoryImpl @Inject constructor(
        private val configuration: Configuration,
        private val fieldFactory: Field.Factory,
        private val shipPlacerFactory: RandomShipPlacer.Factory,
        private val gameEventPublisher: GameEventPublisher
    ) : Factory {
        override fun create(): GameController {
            val myField = fieldFactory.create(configuration)
            val enemyField = fieldFactory.create(configuration)
            return GameController(myField, enemyField, shipPlacerFactory, gameEventPublisher)
        }
    }
}
