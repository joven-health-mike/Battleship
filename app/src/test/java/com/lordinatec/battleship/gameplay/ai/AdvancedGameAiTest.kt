/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.battleship.gameplay.ai

import com.lordinatec.battleship.gameplay.events.GameEvent
import com.lordinatec.battleship.gameplay.events.GameEventPublisher
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdvancedGameAiTest {

    @MockK
    private lateinit var gameEventPublisher: GameEventPublisher

    @MockK
    private lateinit var viewModel: GameViewModel

    private val configuration = Configuration(10, 10)
    private val mockEventFlow = MutableSharedFlow<GameEvent>()
    private val mockTurnFlow = MutableStateFlow(TurnState().copy(isMyTurn = false))
    private val enemyShots = HashSet<FieldIndex>()

    private lateinit var advancedGameAi: AdvancedGameAi

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
            TestScope().launch {
                if (location < 18) {
                    mockEventFlow.emit(GameEvent.EnemyShotHit(location))
                } else {
                    mockEventFlow.emit(GameEvent.EnemyShotMissed(location))
                }
            }
            Unit
        }
        every { gameEventPublisher.eventFlow } answers { mockEventFlow }
        enemyShots.clear()
        advancedGameAi = AdvancedGameAi(viewModel, gameEventPublisher)
        TestScope().launch {
            advancedGameAi.consume()
        }
    }

    @Test
    fun testNoDupes() = runTest {
        for (i in viewModel.enemyFieldIndexRange()) {
            advancedGameAi.makeNextMove()
            verify { viewModel.makeEnemyShot(any()) }
        }
        assertEquals(viewModel.enemyFieldIndexRange().count(), enemyShots.size)
    }
}
