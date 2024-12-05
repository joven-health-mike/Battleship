package com.lordinatec.battleship.gameplay.views

import androidx.compose.ui.graphics.Color

/**
 * The state of a field cell.
 *
 * @param primaryColor The primary color of the cell.
 * @param secondaryColor The secondary color of the cell.
 */
enum class FieldCellState(val primaryColor: Color, val secondaryColor: Color) {
    /**
     * The cell is empty.
     */
    EMPTY(Color.Blue, Color.Blue),

    /**
     * The cell contains a ship.
     */
    SHIP(Color.DarkGray, Color.DarkGray),

    /**
     * The cell was hit.
     */
    HIT(Color.Red, Color.DarkGray),

    /**
     * The cell was missed.
     */
    MISS(Color.White, Color.Blue)
}
