package com.lordinatec.battleship.gameplay.model

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
        override fun create(viewModel: GameViewModel): GameAi {
            return RandomGameAi(viewModel)
        }
    }
}
