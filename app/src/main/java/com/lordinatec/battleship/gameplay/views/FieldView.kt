package com.lordinatec.battleship.gameplay.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.FieldState

/**
 * The view for a field.
 *
 * @param configuration The configuration for the game.
 * @param fieldState The state of the field.
 * @param shouldShowShips Whether to show the ships on the field.
 * @param onFieldClicked The callback for when a field cell is clicked.
 */
@Composable
fun FieldView(
    configuration: Configuration,
    fieldState: FieldState,
    shouldShowShips: Boolean,
    onFieldClicked: (FieldIndex) -> Unit
) {
    Row {
        (0 until configuration.rows).forEach { rowIndex ->
            Column {
                (0 until configuration.columns).forEach { columnIndex ->
                    val index = columnIndex * configuration.columns + rowIndex
                    var fieldCellState = FieldCellState.EMPTY
                    fieldCellState =
                        maybeShowShips(shouldShowShips, fieldState, index) ?: fieldCellState
                    when {
                        fieldState.hits.contains(index) -> {
                            fieldCellState = FieldCellState.HIT
                        }

                        fieldState.misses.contains(index) -> {
                            fieldCellState = FieldCellState.MISS
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

private fun maybeShowShips(
    shouldShowShips: Boolean,
    fieldState: FieldState,
    index: Int
): FieldCellState? {
    if (shouldShowShips) {
        for (shipLocation in fieldState.shipLocations.values) {
            if (shipLocation.contains(index)) {
                return FieldCellState.SHIP
            }
        }
    }
    return null
}

@Preview
@Composable
fun FieldViewPreview() {
    FieldView(
        configuration = Configuration(8, 8),
        fieldState = FieldState(),
        shouldShowShips = true,
        onFieldClicked = {}
    )
}
