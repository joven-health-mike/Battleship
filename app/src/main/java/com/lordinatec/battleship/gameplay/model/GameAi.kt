package com.lordinatec.battleship.gameplay.model

import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel

fun interface GameAi {
    fun makeNextMove()

    fun interface Factory {
        fun create(viewModel: GameViewModel): GameAi
    }
}
