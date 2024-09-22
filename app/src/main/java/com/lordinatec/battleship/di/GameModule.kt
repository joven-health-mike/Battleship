package com.lordinatec.battleship.di

import com.lordinatec.battleship.gameplay.events.EventProvider
import com.lordinatec.battleship.gameplay.events.GameEventPublisher
import com.lordinatec.battleship.gameplay.model.Configuration
import com.lordinatec.battleship.gameplay.model.Field
import com.lordinatec.battleship.gameplay.model.OceanField
import com.lordinatec.battleship.gameplay.model.RandomShipPlacer
import com.lordinatec.battleship.gameplay.viewmodel.GameController
import com.lordinatec.battleship.gameplay.viewmodel.GameViewModel
import com.lordinatec.battleship.logger.LogcatLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Module for providing game related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
class GameModule {

    @Provides
    @Singleton
    fun provideViewModel(
        configuration: Configuration,
        gameControllerFactory: GameController.Factory,
    ): GameViewModel =
        GameViewModel(configuration, gameControllerFactory)

    @Provides
    @Singleton
    fun provideConfiguration(): Configuration = Configuration(rows = 7, columns = 7)

    @Provides
    @Singleton
    fun provideFieldFactory(): OceanField.FactoryImpl = OceanField.FactoryImpl()

    @Provides
    @Singleton
    fun provideIOCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideMainCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideLogcatLogger(
        eventProvider: EventProvider
    ): LogcatLogger = LogcatLogger(eventProvider)

    @Provides
    @Singleton
    fun provideGameEventPublisher(
        scope: CoroutineScope
    ): GameEventPublisher = GameEventPublisher(scope)

    @Provides
    @Singleton
    fun provideRandomShipPlacerFactory(): RandomShipPlacer.Factory = RandomShipPlacer.Factory()

    @Provides
    @Singleton
    fun provideGameControllerFactory(
        configuration: Configuration,
        fieldFactory: Field.Factory,
        shipPlacerFactory: RandomShipPlacer.Factory,
        gameEventPublisher: GameEventPublisher,
    ): GameController.FactoryImpl =
        GameController.FactoryImpl(
            configuration,
            fieldFactory,
            shipPlacerFactory,
            gameEventPublisher
        )
}

@Module
@InstallIn(SingletonComponent::class)
interface InterfaceGameModule {

    @Binds
    fun bindFieldFactory(defaultFieldFactory: OceanField.FactoryImpl): Field.Factory

    @Binds
    fun bindGameControllerFactory(defaultGameControllerFactory: GameController.FactoryImpl): GameController.Factory

    @Binds
    fun bindGameEventPublisher(gameEventPublisher: GameEventPublisher): EventProvider
}
