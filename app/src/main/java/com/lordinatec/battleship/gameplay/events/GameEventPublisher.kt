/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.battleship.gameplay.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * A publisher of GameEvents
 *
 * @property publisherScope The CoroutineScope to use for publishing events
 *
 * @constructor Creates a new GameEventPublisher
 */
class GameEventPublisher @Inject constructor(
    private val publisherScope: CoroutineScope,
) : EventPublisher, EventProvider {
    private val _events = MutableSharedFlow<Event>()
    override val events = _events.asSharedFlow()
    override val eventFlow = events

    /**
     * Publishes a GameEvent
     *
     * @param event The event to publish
     */
    override fun publish(event: Event) {
        // Block to ensure events are published in the order received
        runBlocking {
            publisherScope.launch {
                publishEvent(event as GameEvent)
            }
        }
    }

    private suspend fun publishEvent(event: GameEvent) {
        _events.emit(event)
    }
}
