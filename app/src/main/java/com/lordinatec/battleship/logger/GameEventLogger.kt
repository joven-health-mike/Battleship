/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.battleship.logger

import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.events.GameEvent
import javax.inject.Inject

/**
 * Game event logger - logs game events to system.out. Call consume to start logging.
 *
 * @param eventProvider The provider of game events.
 */
class GameEventLogger @Inject constructor(
    private val eventProvider: EventProvider
) {
    suspend fun consume() {
        eventProvider.eventFlow.collect { event ->
            when (event) {
                is GameEvent.GameCreated -> println("Game created")
                is GameEvent.GameLost -> println("Game lost")
                is GameEvent.GameWon -> println("Game won")
                is GameEvent.MyShotHit -> println("My shot hit: ${event.index}")
                is GameEvent.MyShotMissed -> println("My shot missed: ${event.index}")
                is GameEvent.EnemyShotHit -> println("Enemy shot hit: ${event.index}")
                is GameEvent.EnemyShotMissed -> println("Enemy shot missed: ${event.index}")
            }
        }
    }
}
