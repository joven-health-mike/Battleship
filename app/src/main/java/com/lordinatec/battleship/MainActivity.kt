package com.lordinatec.battleship

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.Ship
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel
import com.lordinatec.battleship.gameplay.views.GameView
import com.lordinatec.battleship.logger.LogcatLogger
import com.lordinatec.battleship.ui.theme.BattleshipTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var configuration: Configuration

    @Inject
    lateinit var logcatLogger: LogcatLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = hiltViewModel()
            BattleshipTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .navigationBarsPadding()
                        .padding(top = 10.dp)
                ) { innerPadding ->
                    Modifier.padding(innerPadding)
                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            logcatLogger.consume()
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        GameView(viewModel, clickListener = { index ->
                            lifecycleScope.launch {
                                runSimulation(viewModel)
                            }
                        })
                    }
                    if (viewModel.turnState().collectAsState().value.isMyTurn) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("My Turn!")
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Enemy Turn!")
                        }
                    }
                    if (viewModel.friendlyFieldState().collectAsState().value.sunk.size == 5) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("You lose!")
                        }
                    }
                    if (viewModel.enemyFieldState().collectAsState().value.sunk.size == 5) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("You win!")
                        }
                    }
                }
            }
        }
    }

    private suspend fun runSimulation(viewModel: GameViewModel) {
        viewModel.placeShip(Ship.CARRIER, (0..4).toSet())
        viewModel.placeShip(
            Ship.BATTLESHIP,
            (configuration.columns..configuration.columns + 3).toSet()
        )
        viewModel.placeShip(
            Ship.CRUISER,
            (configuration.columns * 2..configuration.columns * 2 + 2).toSet()
        )
        viewModel.placeShip(
            Ship.SUBMARINE,
            (configuration.columns * 3..configuration.columns * 3 + 2).toSet()
        )
        viewModel.placeShip(
            Ship.DESTROYER,
            (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
        )
        viewModel.placeEnemyShip(Ship.CARRIER, (0..4).toSet())
        viewModel.placeEnemyShip(
            Ship.BATTLESHIP,
            (configuration.columns..configuration.columns + 3).toSet()
        )
        viewModel.placeEnemyShip(
            Ship.CRUISER,
            (configuration.columns * 2..configuration.columns * 2 + 2).toSet()
        )
        viewModel.placeEnemyShip(
            Ship.SUBMARINE,
            (configuration.columns * 3..configuration.columns * 3 + 2).toSet()
        )
        viewModel.placeEnemyShip(
            Ship.DESTROYER,
            (configuration.columns * 4..configuration.columns * 4 + 1).toSet()
        )
        viewModel.startGame()
        viewModel.makeShot(0)
        delay(1000)
        viewModel.makeEnemyShot(6)
        delay(1000)
        viewModel.makeShot(1)
        delay(1000)
        viewModel.makeEnemyShot(7)
        delay(1000)
        viewModel.makeShot(2)
        delay(1000)
        viewModel.makeEnemyShot(8)
        delay(1000)
        viewModel.makeShot(3)
        delay(1000)
        viewModel.makeEnemyShot(9)
        delay(1000)
        viewModel.makeShot(4)
        delay(1000)
        viewModel.makeEnemyShot(10)
        delay(1000)
        viewModel.makeShot(7)
        delay(1000)
        viewModel.makeEnemyShot(11)
        delay(1000)
        viewModel.makeShot(8)
        delay(1000)
        viewModel.makeEnemyShot(12)
        delay(1000)
        viewModel.makeShot(9)
        delay(1000)
        viewModel.makeEnemyShot(13)
        delay(1000)
        viewModel.makeShot(10)
        delay(1000)
        viewModel.makeEnemyShot(14)
        delay(1000)
        viewModel.makeShot(14)
        delay(1000)
        viewModel.makeEnemyShot(15)
        delay(1000)
        viewModel.makeShot(15)
        delay(1000)
        viewModel.makeEnemyShot(16)
        delay(1000)
        viewModel.makeShot(16)
        delay(1000)
        viewModel.makeEnemyShot(17)
        delay(1000)
        viewModel.makeShot(21)
        delay(1000)
        viewModel.makeEnemyShot(18)
        delay(1000)
        viewModel.makeShot(22)
        delay(1000)
        viewModel.makeEnemyShot(19)
        delay(1000)
        viewModel.makeShot(23)
        delay(1000)
        viewModel.makeEnemyShot(20)
        delay(1000)
        viewModel.makeShot(28)
        delay(1000)
        viewModel.makeEnemyShot(29)
        delay(1000)
        viewModel.makeShot(29)
        delay(1000)
    }
}
