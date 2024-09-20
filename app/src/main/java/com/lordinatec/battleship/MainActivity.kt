package com.lordinatec.battleship

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.lordinatec.battleship.gameplay.Configuration
import com.lordinatec.battleship.gameplay.Ship
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel
import com.lordinatec.battleship.logger.LogcatLogger
import com.lordinatec.battleship.ui.theme.BattleshipTheme
import dagger.hilt.android.AndroidEntryPoint
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            logcatLogger.consume()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            if (viewModel.isGameActive()) return@Button
                            runSimulation(viewModel)
                        }) {
                            Text("Run simulation")
                        }
                    }
                }
            }
        }
    }

    private fun runSimulation(viewModel: GameViewModel) {
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
        viewModel.makeEnemyShot(6)
        viewModel.makeShot(1)
        viewModel.makeEnemyShot(7)
        viewModel.makeShot(2)
        viewModel.makeEnemyShot(8)
        viewModel.makeShot(3)
        viewModel.makeEnemyShot(9)
        viewModel.makeShot(4)
        viewModel.makeEnemyShot(10)
        viewModel.makeShot(10)
        viewModel.makeEnemyShot(11)
        viewModel.makeShot(11)
        viewModel.makeEnemyShot(12)
        viewModel.makeShot(12)
        viewModel.makeEnemyShot(13)
        viewModel.makeShot(13)
        viewModel.makeEnemyShot(14)
        viewModel.makeShot(20)
        viewModel.makeEnemyShot(15)
        viewModel.makeShot(21)
        viewModel.makeEnemyShot(16)
        viewModel.makeShot(22)
        viewModel.makeEnemyShot(17)
        viewModel.makeShot(30)
        viewModel.makeEnemyShot(18)
        viewModel.makeShot(31)
        viewModel.makeEnemyShot(19)
        viewModel.makeShot(32)
        viewModel.makeEnemyShot(20)
        viewModel.makeShot(40)
        viewModel.makeEnemyShot(21)
        viewModel.makeShot(41)
    }
}
