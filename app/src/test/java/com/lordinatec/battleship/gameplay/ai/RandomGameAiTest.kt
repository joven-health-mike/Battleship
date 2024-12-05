/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.battleship.gameplay.ai

import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.TurnState
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RandomGameAiTest {

    @MockK
    private lateinit var viewModel: GameViewModel

    private val configuration = Configuration(10, 10)
    private val mockTurnFlow = MutableStateFlow(TurnState().copy(isMyTurn = false))
    private val enemyShots = HashSet<FieldIndex>()

    private lateinit var randomGameAi: RandomGameAi

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { viewModel.configuration } answers { configuration }
        every { viewModel.enemyFieldIndexRange() } answers { 0 until configuration.columns * configuration.rows }
        every { viewModel.enemyShots() } answers { enemyShots }
        every { viewModel.turnState() } answers { mockTurnFlow }
        every { viewModel.makeEnemyShot(any()) } answers {
            val location = firstArg<FieldIndex>()
            enemyShots.add(location)
            Unit
        }
        enemyShots.clear()
        randomGameAi = RandomGameAi(viewModel)
    }

    @Test
    fun testNoDupes() = runTest {
        for (i in viewModel.enemyFieldIndexRange()) {
            randomGameAi.makeNextMove()
            verify { viewModel.makeEnemyShot(any()) }
        }
        assertEquals(viewModel.enemyFieldIndexRange().count(), enemyShots.size)
    }
}
