package com.lordinatec.battleship.gameplay.model

class RandomShipPlacer(
    private val field: Field
) {

    fun placeAllShips() {
        Ship.entries.forEach { placeShip(it) }
    }

    fun placeShip(ship: Ship) {
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
            println("Ship $ship does not fit on the field")
            placeShip(ship)
            return
        }

        val shipIndices = when (shipDirection) {
            ShipDirection.HORIZONTAL -> shipStartIndex..shipEndIndex
            ShipDirection.VERTICAL -> shipStartIndex.rangeTo(shipEndIndex).step(columns)
        }

        if (shipIndices.any { field.isOccupied(it) }) {
            println("Ship $ship overlaps with another ship")
            placeShip(ship)
            return
        }

        println("Placing ship $ship at $shipStartIndex to $shipEndIndex")
        field.placeShip(ship, shipIndices.toSet())
    }

    class Factory {
        fun create(field: Field): RandomShipPlacer {
            return RandomShipPlacer(field)
        }
    }
}