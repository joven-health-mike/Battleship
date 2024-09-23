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
    fun friendlyFieldState() = gameController.myField.fieldState
    fun enemyFieldState() = gameController.enemyField.fieldState
    fun turnState() = gameController.turnState
    fun enemyShots() = gameController.enemyShots

    /* PUBLIC APIS */
    /**
     * Clear the game state and reset the game.
     */
    fun resetGame() {
        gameController = gameControllerFactory.create()
    }

    fun startGame() {
        gameController.startGame()
    }

    fun isGameActive(): Boolean {
        return gameController.isGameActive()
    }

    fun placeShipsAtRandom() {
        gameController.placeShipsAtRandom()
    }

    fun placeEnemyShipsAtRandom() {
        gameController.placeEnemyShipsAtRandom()
    }

    fun placeShip(ship: Ship, locations: Set<FieldIndex>) {
        gameController.placeShip(ship, locations)
    }

    fun placeEnemyShip(ship: Ship, locations: Set<FieldIndex>) {
        gameController.placeEnemyShip(ship, locations)
    }

    fun fieldIndexRange() = gameController.fieldIndexRange()
    fun enemyFieldIndexRange() = gameController.enemyFieldIndexRange()

    /**
     * Make a shot at the given index.
     *
     * @param index The field index at which to shoot.
     */
    fun makeShot(index: Int) {
        gameController.shootAtEnemy(index)
    }

    /**
     * Make an enemy shot at the given index.
     *
     * @param index The field index at which to shoot.
     */
    fun makeEnemyShot(index: Int) {
        gameController.enemyShot(index)
    }
}
