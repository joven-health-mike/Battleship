package com.lordinatec.battleship.gameplay.model

/**
 * Represents the state of the turn in the game.
 */
data class TurnState(
    /**
     * Whether it is the player's turn.
     */
    val isMyTurn: Boolean = true,
    /**
     * Whether the game is over.
     */
    val isGameOver: Boolean = false
)
