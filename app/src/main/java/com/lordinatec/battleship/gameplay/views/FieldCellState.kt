package com.lordinatec.battleship.gameplay.views

import androidx.compose.ui.graphics.Color

enum class FieldCellState(val primaryColor: Color, val secondaryColor: Color) {
    EMPTY(Color.Gray, Color.Blue),
    SHIP(Color.DarkGray, Color.Blue),
    HIT(Color.Red, Color.Blue),
    MISS(Color.White, Color.Blue)
}
