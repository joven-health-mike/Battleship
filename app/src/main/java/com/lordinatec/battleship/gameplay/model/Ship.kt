package com.lordinatec.battleship.gameplay.model

/**
 * Enum class representing the different types of ships in the game.
 *
 * @property length The length of the ship.
 *
 * @constructor Creates a new ship with the given length.
 */
enum class Ship(val length: ShipLength) {
    /**
     * The aircraft carrier ship.
     */
    CARRIER(5),

    /**
     * The battleship ship.
     */
    BATTLESHIP(4),

    /**
     * The cruiser ship.
     */
    CRUISER(3),

    /**
     * The submarine ship.
     */
    SUBMARINE(3),

    /**
     * The destroyer ship.
     */
    DESTROYER(2)
}

/**
 * Enum class representing the different directions a ship can be placed on the field.
 */
enum class ShipDirection {
    HORIZONTAL,
    VERTICAL
}
