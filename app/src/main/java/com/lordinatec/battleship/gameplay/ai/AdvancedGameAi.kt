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
                is GameEvent.EnemyShotHit -> shotResults[gameEvent.index] = true
                is GameEvent.EnemyShotMissed -> shotResults[gameEvent.index] = false
                is GameEvent.ShipSunk -> {
                    sunkShips.add(gameEvent.ship)
                    bestGuess.clear()
                }
            }
            analyzeField()
        }
    }

    override fun makeNextMove() {
        cleanBestGuess()
        viewModel.state.value.turnState.apply {
            if (!isMyTurn) {
                val guess = bestGuess.random()
                viewModel.makeEnemyShot(guess)
                shotHistory.add(guess)
            }
        }
    }

    private fun analyzeField() {
        // if we get 2 hits in a row, add the next field in the same direction
        shotResults.keys.filter { shotResults[it] == true }.forEach { index ->
            var foundDouble = false
            if (shotResults[index + 1] == true) {
                if (shotResults[index + 2] == true) {
                    bestGuess.add(index + 3)
                } else {
                    bestGuess.add(index + 2)
                }
                foundDouble = true
            }
            if (shotResults[index - 1] == true) {
                if (shotResults[index - 2] == true) {
                    bestGuess.add(index - 3)
                } else {
                    bestGuess.add(index - 2)
                }
                foundDouble = true
            }
            if (shotResults[index + viewModel.configuration.rows] == true) {
                if (shotResults[index + viewModel.configuration.rows * 2] == true) {
                    bestGuess.add(index + viewModel.configuration.rows * 3)
                } else {
                    bestGuess.add(index + viewModel.configuration.rows * 2)
                }
                foundDouble = true
            }
            if (shotResults[index - viewModel.configuration.rows] == true) {
                if (shotResults[index - viewModel.configuration.rows * 2] == true) {
                    bestGuess.add(index - viewModel.configuration.rows * 3)
                } else {
                    bestGuess.add(index - viewModel.configuration.rows * 2)
                }
                foundDouble = true
            }
            if (!foundDouble) {
                bestGuess.add(index + 1)
                bestGuess.add(index - 1)
                bestGuess.add(index + viewModel.configuration.rows)
                bestGuess.add(index - viewModel.configuration.rows)
            }
        }
    }

    private fun cleanBestGuess() {
        // remove any shots that were already taken or out of bounds
        bestGuess.removeAll(shotHistory)
        bestGuess.retainAll(viewModel.enemyFieldIndexRange())

        if (bestGuess.isEmpty()) {
            bestGuess.add(randomFieldIndex())
        }
    }

    private fun randomFieldIndex(): FieldIndex {
        val range = viewModel.enemyFieldIndexRange()
        val index = range.filterNot { viewModel.state.value.enemyShots.contains(it) }.random()
        return index
    }

    class Factory : GameAi.Factory {
        override fun create(viewModel: GameViewModel, eventProvider: EventProvider?): GameAi {
            return AdvancedGameAi(viewModel, eventProvider!!)
        }
    }
}
