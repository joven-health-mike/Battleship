/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.battleship.logger

import android.util.Log
import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.events.GameEvent
import javax.inject.Inject

/**
 * Logs game events to Logcat. Logger starts immediately on creation.
 *
 * @param eventProvider The provider of game events.
 */
class LogcatLogger @Inject constructor(
    private val eventProvider: EventProvider
) {
    // change this to change the logging level
    private val level = debug

    suspend fun consume() {
        eventProvider.eventFlow.collect { event ->
            when (event) {
                is GameEvent.GameCreated -> level("Game created")
                is GameEvent.GameLost -> level("Game lost")
                is GameEvent.GameWon -> level("Game won")
                is GameEvent.MyShotHit -> level("My shot hit: ${event.index}")
                is GameEvent.MyShotMissed -> level("My shot missed: ${event.index}")
                is GameEvent.ShipSunk -> level("Enemy sunk my ship: ${event.ship}")
                is GameEvent.EnemyShotHit -> level("Enemy shot hit: ${event.index}")
                is GameEvent.EnemyShotMissed -> level("Enemy shot missed: ${event.index}")
                is GameEvent.EnemyShipSunk -> level("I sunk enemy ship: ${event.ship}")
            }
        }
    }

    companion object {
        private val debug: (s: String) -> Unit = { s -> Log.d("Battleship", s) }
        private val info: (s: String) -> Unit = { s -> Log.i("Battleship", s) }
        private val verbose: (s: String) -> Unit = { s -> Log.v("Battleship", s) }
    }
}
