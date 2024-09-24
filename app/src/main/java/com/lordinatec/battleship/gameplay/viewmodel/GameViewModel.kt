/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.battleship.gameplay.viewmodel

import androidx.lifecycle.ViewModel
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.Ship
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Game screen.
 *
 * @param configuration The configuration for the game.
 * @param gameControllerFactory A factory for creating a GameController.
 *
 * @return A ViewModel for the Game screen.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    val configuration: Configuration,
    private val gameControllerFactory: GameController.Factory
) : ViewModel() {

    private var gameController = gameControllerFactory.create()

    /* PUBLIC APIS */
    /**
     * Get the friendly field state.
     */
    fun friendlyFieldState() = gameController.myField.fieldState

    /**
     * Get the enemy field state.
     */
    fun enemyFieldState() = gameController.enemyField.fieldState

    /**
     * Get the state of the turn.
     */
    fun turnState() = gameController.turnState

    /**
     * Get the shots made by the enemy.
     */
    fun enemyShots() = gameController.enemyShots

    /**
     * Clear the game state and reset the game.
     */
    fun resetGame() {
        gameController = gameControllerFactory.create()
    }

    /**
     * Start the game.
     */
    fun startGame() {
        gameController.startGame()
    }

    /**
     * Check if the game is active.
     */
    fun isGameActive(): Boolean {
        return gameController.isGameActive()
    }

    /**
     * Place ships at random.
     */
    fun placeShipsAtRandom() {
        gameController.placeShipsAtRandom()
    }

    /**
     * Place enemy ships at random.
     */
    fun placeEnemyShipsAtRandom() {
        gameController.placeEnemyShipsAtRandom()
    }

    /**
     * Place a ship at the given locations.
     *
     * @param ship The ship to place.
     * @param locations The locations at which to place the ship.
     */
    fun placeShip(ship: Ship, locations: Set<FieldIndex>) {
        gameController.placeShip(ship, locations)
    }

    /**
     * Place an enemy ship at the given locations.
     *
     * @param ship The ship to place.
     * @param locations The locations at which to place the ship.
     */
    fun placeEnemyShip(ship: Ship, locations: Set<FieldIndex>) {
        gameController.placeEnemyShip(ship, locations)
    }

    /**
     * Get the player field index range.
     */
    fun fieldIndexRange() = gameController.fieldIndexRange()

    /**
     * Get the enemy field index range.
     */
    fun enemyFieldIndexRange() = gameController.enemyFieldIndexRange()

    /**
     * Make a shot at the given index.
     *
     * @param index The field index at which to shoot.
     */
    fun makeShot(index: FieldIndex) {
        gameController.shootAtEnemy(index)
    }

    /**
     * Make an enemy shot at the given index.
     *
     * @param index The field index at which to shoot.
     */
    fun makeEnemyShot(index: FieldIndex) {
        gameController.enemyShot(index)
    }
}
