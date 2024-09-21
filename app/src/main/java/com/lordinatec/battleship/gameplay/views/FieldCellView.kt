package com.lordinatec.battleship.gameplay.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lordinatec.battleship.gameplay.model.FieldIndex

@Composable
fun FieldCellView(
    index: FieldIndex,
    fieldCellState: FieldCellState,
    onFieldClicked: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .border(3.dp, Color.Black)
            .clickable(onClick = { onFieldClicked(index) })
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        fieldCellState.primaryColor, fieldCellState.secondaryColor
                    )
                )
            ),
    )
}

@Preview
@Composable
fun FieldCellViewPreview() {
    FieldCellView(index = 0, fieldCellState = FieldCellState.EMPTY, onFieldClicked = {})
}
