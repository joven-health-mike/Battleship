package com.lordinatec.battleship.gameplay.model

data class TurnState(
    val isMyTurn: Boolean = true,
    val isGameOver: Boolean = false
)
