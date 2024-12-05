package com.lordinatec.battleship.gameplay.viewmodel

import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.FieldState
import com.lordinatec.battleship.gameplay.model.TurnState

data class GameViewModelState(
    val friendlyFieldState: FieldState = FieldState(),
    val enemyFieldState: FieldState = FieldState(),
    val turnState: TurnState = TurnState(),
    val enemyShots: Set<FieldIndex> = emptySet(),
    val shots: Set<FieldIndex> = emptySet(),
)
