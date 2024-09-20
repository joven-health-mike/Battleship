package com.lordinatec.battleship.gameplay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Represents a field in the game of Battleship.
 *
 * @param configuration The configuration of the field.
 *
 * @constructor Creates a new field with the specified configuration.
 */
class OceanField(override var configuration: Configuration) : Field {

    private val _fieldState = MutableStateFlow(FieldState())
    override val fieldState: StateFlow<FieldState>
        get() = _fieldState.asStateFlow()

    override fun reset(configuration: Configuration) {
        if (configuration != this.configuration) this.configuration = configuration
        _fieldState.update { FieldState() }
    }

    override fun shoot(index: FieldIndex): Hit {
        require(fieldState.value.shipLocations.size == Ship.entries.size) { "Ships have not yet been placed." }
        val fieldState = fieldState.value
        fieldState.shipLocations.forEach { (ship, locations) ->
            if (locations.contains(index)) {
                _fieldState.update {
                    val newHits = it.hits + index
                    var newSunk = it.sunk
                    if (newHits.containsAll(locations)) {
                        newSunk += ship
                    }
                    it.copy(
                        hits = newHits,
                        sunk = newSunk
                    )
                }
                return true
            }
        }

        _fieldState.update {
            it.copy(
                misses = it.misses + index
            )
        }
        return false
    }

    override fun placeShip(ship: Ship, location: Set<FieldIndex>) {
        require(!fieldState.value.shipLocations.containsKey(ship)) { "Ship has already been placed." }
        require(ship.length == location.size) { "Ship length does not match location size." }
        require(location.all { it in fieldIndexRange() }) { "Location is out of bounds." }
        require(fieldState.value.shipLocations.all {
            it.value.intersect(location).isEmpty()
        }) { "Ship locations overlap." }
        _fieldState.update {
            it.copy(
                shipLocations = fieldState.value.shipLocations + (ship to location)
            )
        }
    }
}
