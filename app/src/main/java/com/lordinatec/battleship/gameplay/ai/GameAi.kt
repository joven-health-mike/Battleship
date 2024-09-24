package com.lordinatec.battleship.gameplay.ai

import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel

/**
 * Interface for a game AI that can play the game.
 */
fun interface GameAi {
    /**
     * Makes the next move in the game.
     */
    fun makeNextMove()

    /**
     * Factory interface for creating game AIs.
     */
    fun interface Factory {
        /**
         * Creates a new game AI with the given view model and event provider.
         */
        fun create(viewModel: GameViewModel, eventProvider: EventProvider?): GameAi
    }
}
