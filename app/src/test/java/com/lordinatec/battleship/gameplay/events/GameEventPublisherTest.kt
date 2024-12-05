/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.battleship.gameplay.events

import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GameEventPublisherTest {

    private lateinit var gameEventPublisher: GameEventPublisher

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        gameEventPublisher = GameEventPublisher(TestScope())
    }

    @Test
    fun testMyShotHit() = runTest {
        val index = 0
        gameEventPublisher.publish(GameEvent.MyShotHit(index))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.MyShotHit)
            assertEquals(index, (it as GameEvent.MyShotHit).index)
        }
    }

    @Test
    fun testMyShotMissed() = runTest {
        val index = 0
        gameEventPublisher.publish(GameEvent.MyShotMissed(index))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.MyShotMissed)
            assertEquals(index, (it as GameEvent.MyShotMissed).index)
        }
    }

    @Test
    fun testEnemyShotHit() = runTest {
        val index = 0
        gameEventPublisher.publish(GameEvent.EnemyShotHit(index))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.EnemyShotHit)
            assertEquals(index, (it as GameEvent.EnemyShotHit).index)
        }
    }

    @Test
    fun testEnemyShotMissed() = runTest {
        val index = 0
        gameEventPublisher.publish(GameEvent.EnemyShotMissed(index))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.EnemyShotMissed)
            assertEquals(index, (it as GameEvent.EnemyShotMissed).index)
        }
    }

    @Test
    fun testGameWon() = runTest {
        gameEventPublisher.publish(GameEvent.GameWon)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.GameWon)
        }
    }

    @Test
    fun testGameLost() = runTest {
        gameEventPublisher.publish(GameEvent.GameLost)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.GameLost)
        }
    }
}
