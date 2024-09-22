package com.lordinatec.battleship.gameplay.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lordinatec.battleship.gameplay.events.GameEventPublisher
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.model.OceanField
import com.lordinatec.battleship.gameplay.model.RandomShipPlacer
import com.lordinatec.battleship.gameplay.viewmodel.GameController
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Composable
fun GameView(viewModel: GameViewModel, clickListener: (index: FieldIndex) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Enemy Field")
        FieldView(
            configuration = viewModel.configuration,
            fieldState = viewModel.enemyFieldState().collectAsState().value,
            shouldShowShips = false,
            onFieldClicked = clickListener
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text("My Field")
        FieldView(
            configuration = viewModel.configuration,
            fieldState = viewModel.friendlyFieldState().collectAsState().value,
            shouldShowShips = true,
            onFieldClicked = clickListener
        )

    }
}

@Preview
@Composable
fun GameViewPreview() {
    val configuration = Configuration(8, 8)
    val myField = OceanField(configuration)
    val enemyField = OceanField(configuration)
    val gameEventPublisher = GameEventPublisher(CoroutineScope(SupervisorJob()))
    GameView(
        viewModel = GameViewModel(
            configuration
        ) { GameController(myField, enemyField, RandomShipPlacer.Factory(), gameEventPublisher) },
        clickListener = {}
    )
}
