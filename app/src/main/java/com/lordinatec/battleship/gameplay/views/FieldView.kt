package com.lordinatec.battleship.gameplay.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.FieldState

@Composable
fun FieldView(
    configuration: Configuration,
    fieldState: FieldState,
    onFieldClicked: (FieldIndex) -> Unit
) {
    Row {
        (0 until configuration.rows).forEach { rowIndex ->
            Column {
                (0 until configuration.columns).forEach { columnIndex ->
                    val index = columnIndex * configuration.columns + rowIndex
                    var fieldCellState: FieldCellState = FieldCellState.EMPTY
                    when {
                        fieldState.hits.contains(index) -> {
                            fieldCellState = FieldCellState.HIT
                        }

                        fieldState.misses.contains(index) -> {
                            fieldCellState = FieldCellState.MISS
                        }

                        fieldState.shipLocations.values.contains<Any>(index) -> {
                            fieldCellState = FieldCellState.SHIP
                        }
                    }
                    FieldCellView(
                        index = index,
                        fieldCellState = fieldCellState,
                        onFieldClicked = onFieldClicked
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun FieldViewPreview() {
    FieldView(
        configuration = Configuration(8, 8),
        fieldState = FieldState(),
        onFieldClicked = {}
    )
}