package com.lordinatec.battleship.gameplay.ai

import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel

fun interface GameAi {
    fun makeNextMove()

    fun interface Factory {
        fun create(viewModel: GameViewModel, eventProvider: EventProvider?): GameAi
    }
}
