package com.lordinatec.battleship

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.lordinatec.battleship.gameplay.ai.AdvancedGameAi
import com.lordinatec.battleship.gameplay.ai.GameAi
import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.events.GameEvent
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.FieldIndex
import com.lordinatec.battleship.gameplay.viewmodel.AlreadyShotException
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel
import com.lordinatec.battleship.gameplay.viewmodel.WrongTurnException
import com.lordinatec.battleship.gameplay.views.GameView
import com.lordinatec.battleship.logger.LogcatLogger
import com.lordinatec.battleship.ui.theme.BattleshipTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main activity for the Battleship game.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var configuration: Configuration

    @Inject
    lateinit var logcatLogger: LogcatLogger

    @Inject
    lateinit var gameEventProvider: EventProvider

    @Inject
    lateinit var gameAiFactory: GameAi.Factory

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
                    StartGameEffect(viewModel)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        GameView(
                            viewModel,
                            clickListener = { index -> myClickListener(viewModel, index) },
                            enemyClickListener = { index ->
                                enemyClickListener(viewModel, index)
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

    private fun myClickListener(viewModel: GameViewModel, index: FieldIndex) {
        try {
            viewModel.makeShot(index)
        } catch (e: Exception) {
            when (e) {
                is WrongTurnException -> {
                    Toast.makeText(
                        this@MainActivity,
                        "It's not your turn!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AlreadyShotException -> {
                    Toast.makeText(
                        this@MainActivity,
                        "You already shot here!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> throw e
            }
        }
    }

    private fun enemyClickListener(viewModel: GameViewModel, index: FieldIndex) {
        try {
            viewModel.makeEnemyShot(index)
        } catch (e: Exception) {
            when (e) {
                is WrongTurnException -> {
                    Toast.makeText(
                        this@MainActivity,
                        "It's your turn!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AlreadyShotException -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Enemy already shot here!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> throw e
            }
        }
    }

    @Composable
    fun StartGameEffect(viewModel: GameViewModel) {
        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                logcatLogger.consume()
            }

            lifecycleScope.launch {
                gameEventProvider.eventFlow.collect { event ->
                    when (val gameEvent = event as GameEvent) {
                        is GameEvent.ShipSunk -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Enemy sunk your ${gameEvent.ship}!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is GameEvent.EnemyShipSunk -> {
                            Toast.makeText(
                                this@MainActivity,
                                "You sunk the enemy's ${gameEvent.ship}!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            val gameAi = gameAiFactory.create(viewModel, gameEventProvider)
            lifecycleScope.launch {
                (gameAi as AdvancedGameAi).consume()
            }

            lifecycleScope.launch {
                viewModel.turnState().collect {
                    if (!it.isGameOver && !it.isMyTurn) {
                        delay(200)
                        gameAi.makeNextMove()
                    }
                }
            }

            viewModel.placeShipsAtRandom()
            viewModel.placeEnemyShipsAtRandom()
            viewModel.startGame()
        }
    }
}
