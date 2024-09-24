package com.lordinatec.battleship.gameplay.model

/**
 * Places ships randomly on a field.
 *
 * @param field The field on which to place the ships.
 *
 * @constructor Creates a new random ship placer for the specified field.
 */
class RandomShipPlacer(
    private val field: Field
) {

    /**
     * Places all ships on the field.
     */
    fun placeAllShips() {
        Ship.entries.forEach { placeShip(it) }
    }

    /**
     * Places a ship on the field.
     *
     * @param ship The ship to place.
     */
    private fun placeShip(ship: Ship) {
        val columns = field.configuration.columns
        val shipDirection =
            if (Math.random() < 0.5) ShipDirection.HORIZONTAL else ShipDirection.VERTICAL

        val shipStartIndex = field.fieldIndexRange().random()
        val shipStartRow = shipStartIndex / columns
        val shipStartCol = shipStartIndex % columns

        val shipEndIndex = when (shipDirection) {
            ShipDirection.HORIZONTAL -> shipStartIndex + ship.length - 1
            ShipDirection.VERTICAL -> shipStartIndex + ship.length * columns - columns
        }
        val shipEndRow = shipEndIndex / columns
        val shipEndCol = shipEndIndex % columns

        if (shipEndIndex >= field.fieldSize()
            || (shipDirection == ShipDirection.HORIZONTAL && shipStartRow != shipEndRow)
            || (shipDirection == ShipDirection.VERTICAL && shipStartCol != shipEndCol)
        ) {
            placeShip(ship)
            return
        }

        val shipIndices = when (shipDirection) {
            ShipDirection.HORIZONTAL -> shipStartIndex..shipEndIndex
            ShipDirection.VERTICAL -> shipStartIndex.rangeTo(shipEndIndex).step(columns)
        }

        if (shipIndices.any { field.isOccupied(it) }) {
            placeShip(ship)
            return
        }

        field.placeShip(ship, shipIndices.toSet())
    }

    /**
     * A factory for creating random ship placers.
     */
    class Factory {
        /**
         * Creates a random ship placer for the specified field.
         *
         * @param field The field on which to place the ships.
         *
         * @return A random ship placer for the specified field.
         */
        fun create(field: Field): RandomShipPlacer {
            return RandomShipPlacer(field)
        }
    }
}