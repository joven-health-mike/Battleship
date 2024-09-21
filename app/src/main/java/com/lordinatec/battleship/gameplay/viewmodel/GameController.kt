package com.lordinatec.battleship.gameplay.viewmodel

import com.lordinatec.battleship.gameplay.events.GameEvent
import com.lordinatec.battleship.gameplay.events.GameEventPublisher
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.Field
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.Ship
import com.lordinatec.battleship.gameplay.model.TurnState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GameController(
    val myField: Field,
    val enemyField: Field,
    private val gameEventPublisher: GameEventPublisher
) {
    private val _turnState = MutableStateFlow(TurnState())
    val turnState = _turnState.asStateFlow()

    private val defaultConfiguration = Configuration(10, 10)

    private var gameStarted = false
    private var gameEnded = false

    fun placeShip(ship: Ship, locations: Set<FieldIndex>) {
        require(!gameStarted) { "Game has already started." }
        myField.placeShip(ship, locations)
    }

    fun placeEnemyShip(ship: Ship, locations: Set<FieldIndex>) {
        require(!gameStarted) { "Game has already started." }
        enemyField.placeShip(ship, locations)
    }

    fun startGame() {
        require(!gameStarted) { "Game has already started." }
        require(myField.fieldState.value.shipLocations.size == Ship.entries.size) { "Not all friendly ships have been placed." }
        require(enemyField.fieldState.value.shipLocations.size == Ship.entries.size) { "Not all enemy ships have been placed." }
        gameStarted = true
        gameEnded = false
        gameEventPublisher.publish(GameEvent.GameCreated)
    }

    fun shootAtEnemy(location: FieldIndex) {
        require(isGameActive()) { "Game is not active." }
        require(turnState.value.isMyTurn) { "It is not your turn." }
        val hit = enemyField.shoot(location)
        if (hit) {
            gameEventPublisher.publish(GameEvent.MyShotHit(location))
        } else {
            gameEventPublisher.publish(GameEvent.MyShotMissed(location))
        }
        maybeEndGame()
        _turnState.update { it.copy(isMyTurn = false) }
    }

    fun enemyShot(location: FieldIndex) {
        require(isGameActive()) { "Game is not active." }
        require(!turnState.value.isMyTurn) { "It is not your enemy's turn." }
        val hit = myField.shoot(location)
        if (hit) {
            gameEventPublisher.publish(GameEvent.EnemyShotHit(location))
        } else {
            gameEventPublisher.publish(GameEvent.EnemyShotMissed(location))
        }
        maybeEndGame()
        _turnState.update { it.copy(isMyTurn = true) }
    }

    fun isGameActive() = gameStarted && !gameEnded

    private fun maybeEndGame() {
        if (myField.areAllShipsSunk()) {
            gameEnded = true
            gameEventPublisher.publish(GameEvent.GameLost)
        } else if (enemyField.areAllShipsSunk()) {
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
        private val gameEventPublisher: GameEventPublisher
    ) : Factory {
        override fun create(): GameController {
            val myField = fieldFactory.create(configuration)
            val enemyField = fieldFactory.create(configuration)
            return GameController(myField, enemyField, gameEventPublisher)
        }
    }
}
