package com.lordinatec.battleship.gameplay.ai

import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.events.GameEvent
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.Ship
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel

/**
 * An advanced game AI that uses a more sophisticated strategy to play the game.
 *
 * @property viewModel The view model for the game.
 * @property eventProvider The event provider for the game.
 *
 * @constructor Creates a new advanced game AI with the given view model and event provider.
 */
class AdvancedGameAi(
    private val viewModel: GameViewModel,
    private val eventProvider: EventProvider
) : GameAi {

    private var bestGuess = mutableListOf(randomFieldIndex())
    private val shotHistory = mutableListOf<FieldIndex>()
    private val shotResults = mutableMapOf<FieldIndex, Boolean>()
    private val sunkShips = mutableListOf<Ship>()

    suspend fun consume() {
        eventProvider.eventFlow.collect { event ->
            when (val gameEvent = event as GameEvent) {
                is GameEvent.EnemyShotHit -> {
                    shotResults[gameEvent.index] = true
                    analyzeField()
                }

                is GameEvent.EnemyShotMissed -> {
                    shotResults[gameEvent.index] = false
                    analyzeField()
                }

                is GameEvent.ShipSunk -> {
                    sunkShips.add(gameEvent.ship)
                    bestGuess.clear()
                    ensureBestGuessIsNotEmpty()
                }
            }
        }
    }

    override fun makeNextMove() {
        viewModel.turnState().value.apply {
            if (!isMyTurn) {
                viewModel.makeEnemyShot(bestGuess[0])
                shotHistory.add(bestGuess[0])
            }
        }
    }

    private fun analyzeField() {
        val lastShot = shotHistory.last()
        if (shotResults[lastShot] == true) {
            bestGuess.add(lastShot + 1)
            bestGuess.add(lastShot - 1)
            bestGuess.add(lastShot + viewModel.configuration.rows)
            bestGuess.add(lastShot - viewModel.configuration.rows)
        }

        // remove any shots that were already taken or out of bounds
        bestGuess.removeAll(shotHistory)
        bestGuess.retainAll(viewModel.enemyFieldIndexRange())

        ensureBestGuessIsNotEmpty()
    }

    private fun ensureBestGuessIsNotEmpty() {
        if (bestGuess.isEmpty()) {
            bestGuess.add(randomFieldIndex())
        }
    }

    private fun randomFieldIndex(): FieldIndex {
        val range = viewModel.enemyFieldIndexRange()
        val index = range.filterNot { viewModel.enemyShots().contains(it) }.random()
        return index
    }

    class Factory : GameAi.Factory {
        override fun create(viewModel: GameViewModel, eventProvider: EventProvider?): GameAi {
            return AdvancedGameAi(viewModel, eventProvider!!)
        }
    }
}
