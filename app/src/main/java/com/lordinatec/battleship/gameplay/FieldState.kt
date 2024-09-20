package com.lordinatec.battleship.gameplay

/**
 * Data class representing the state of a field in the game.
 */
data class FieldState(
    /**
     * The hits on the field.
     */
    val hits: Collection<FieldIndex> = emptyList(),

    /**
     * The misses on the field.
     */
    val misses: Collection<FieldIndex> = emptyList(),

    /**
     * The ships that have been sunk.
     */
    val sunk: Collection<Ship> = emptyList(),

    /**
     * The locations of the ships on the field.
     */
    val shipLocations: Map<Ship, List<FieldIndex>> = emptyMap()
)
