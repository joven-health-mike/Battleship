package com.lordinatec.battleship.gameplay

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface representing a field in the game.
 */
interface Field {
    /**
     * The configuration of the field.
     */
    val configuration: Configuration

    /**
     * The state of the field.
     */
    val fieldState: StateFlow<FieldState>

    /**
     * Resets the field with the given configuration.
     */
    fun reset(configuration: Configuration)

    /**
     * Shoots at the field at the given index.
     *
     * @param index The index to shoot at.
     *
     * @return The result of the shot.
     */
    fun shoot(index: FieldIndex): Hit

    /**
     * Places a ship on the field.
     *
     * @param ship The ship to place.
     * @param location The location to place the ship at.
     */
    fun placeShip(ship: Ship, location: List<FieldIndex>)

    /**
     * Checks if all ships on the field have been sunk.
     *
     * @return `true` if all ships have been sunk, `false` otherwise.
     */
    fun areAllShipsSunk(): Boolean = fieldState.value.sunk.size == Ship.entries.size

    /**
     * Returns the size of the field.
     *
     * @return The size of the field.
     */
    fun fieldSize(): Int = configuration.rows * configuration.columns

    /**
     * Returns the range of field indices.
     *
     * @return The range of field indices.
     */
    fun fieldIndexRange(): IntRange = 0 until fieldSize()
}
