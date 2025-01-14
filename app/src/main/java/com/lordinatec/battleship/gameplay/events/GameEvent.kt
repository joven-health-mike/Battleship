/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.battleship.gameplay.events

import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.Ship

/**
 * Interface for game events.
 */
interface GameEvent : Event {
    /**
     * Event for when a game is created.
     */
    object GameCreated : GameEvent

    /**
     * Event for when a game is won.
     */
    object GameWon : GameEvent

    /**
     * Event for when a game is lost.
     */
    object GameLost : GameEvent

    /**
     * Event for when a shot is hit.
     *
     * @param index The index of the position.
     */
    data class MyShotHit(val index: FieldIndex) : GameEvent

    /**
     * Event for when a shot is missed.
     *
     * @param index The index of the position.
     */
    data class MyShotMissed(val index: Int) : GameEvent

    /**
     * Event for when an enemy shot is hit.
     *
     * @param index The index of the position.
     */
    data class EnemyShotHit(val index: FieldIndex) : GameEvent

    /**
     * Event for when an enemy shot is missed.
     *
     * @param index The index of the position.
     */
    data class EnemyShotMissed(val index: Int) : GameEvent

    /**
     * Event for when a ship is sunk.
     *
     * @param ship The ship that was sunk.
     */
    data class ShipSunk(val ship: Ship): GameEvent

    /**
     * Event for when an enemy ship is sunk.
     *
     * @param ship The ship that was sunk.
     */
    data class EnemyShipSunk(val ship: Ship): GameEvent
}
