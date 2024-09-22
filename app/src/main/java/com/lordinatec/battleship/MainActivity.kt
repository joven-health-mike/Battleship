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
                    val turnState = viewModel.turnState().collectAsState().value
                    if (!turnState.isGameOver && turnState.isMyTurn) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("My Turn!")
                        }
                    } else if (!turnState.isGameOver) {
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
        viewModel.placeShipsAtRandom()
        viewModel.placeEnemyShipsAtRandom()
        viewModel.startGame()
        val shotsTaken = mutableListOf<Int>()
        val enemyShotsTaken = mutableListOf<Int>()
        while (viewModel.isGameActive()) {
            delay(500)
            var randomIndex: Int
            do {
                randomIndex = viewModel.fieldIndexRange().random()
            } while (randomIndex in shotsTaken)
            viewModel.makeShot(randomIndex)
            shotsTaken.add(randomIndex)

            if (viewModel.isGameActive()) {
                delay(500)
                do {
                    randomIndex = viewModel.enemyFieldIndexRange().random()
                } while (randomIndex in enemyShotsTaken)
                viewModel.makeEnemyShot(randomIndex)
                enemyShotsTaken.add(randomIndex)
            }
        }
    }
}
