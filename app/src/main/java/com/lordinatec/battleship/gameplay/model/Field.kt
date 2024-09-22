package com.lordinatec.battleship.gameplay.model

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
     * Shoots at the field at the given index.
     *
     * @param index The index to shoot at.
     *
     * @return The result of the shot.
     */
    fun shoot(index: FieldIndex): ShotResult

    /**
     * Places a ship on the field.
     *
     * @param ship The ship to place.
     * @param location The location to place the ship at.
     */
    fun placeShip(ship: Ship, location: Set<FieldIndex>)

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
    fun fieldIndexRange(): FieldIndexRange = 0 until fieldSize()

    /**
     * Checks if the field at the given index is occupied.
     *
     * @param index The index to check.
     *
     * @return `true` if the field is occupied, `false` otherwise.
     */
    fun isOccupied(index: FieldIndex): Boolean {
        fieldState.value.shipLocations.values.forEach {
            if (it.contains(index)) {
                return true
            }
        }
        return false
    }

    /**
     * Factory for creating fields.
     */
    fun interface Factory {
        /**
         * Creates a field with the given configuration.
         *
         * @param configuration The configuration of the field.
         *
         * @return The created field.
         */
        fun create(configuration: Configuration): Field
    }

    class ShotResult(
        val hit: Boolean,
        val sunk: Ship?
    )
}
