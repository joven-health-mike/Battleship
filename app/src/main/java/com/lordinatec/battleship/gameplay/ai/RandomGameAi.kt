package com.lordinatec.battleship.gameplay.ai

import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel

class RandomGameAi(
    private val viewModel: GameViewModel
) : GameAi {
    override fun makeNextMove() {
        viewModel.turnState().value.apply {
            if (!isMyTurn) {
                viewModel.makeEnemyShot(randomFieldIndex())
            }
        }
    }

    private fun randomFieldIndex(): FieldIndex {
        val range = viewModel.enemyFieldIndexRange()
        var index = range.random()
        while (viewModel.enemyShots().contains(index)) {
            index = range.random()
        }
        return index
    }

    class Factory : GameAi.Factory {
        override fun create(viewModel: GameViewModel, eventProvider: EventProvider?): GameAi {
            return RandomGameAi(viewModel)
        }
    }
}