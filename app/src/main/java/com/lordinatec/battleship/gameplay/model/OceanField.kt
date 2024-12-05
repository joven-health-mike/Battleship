package com.lordinatec.battleship.gameplay.model

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

    override fun shoot(index: FieldIndex): Field.ShotResult {
        require(fieldState.value.shipLocations.size == Ship.entries.size) { "Ships have not yet been placed." }
        val fieldState = fieldState.value
        var shipWasSunk = false
        fieldState.shipLocations.forEach { (ship, locations) ->
            if (locations.contains(index)) {
                _fieldState.update {
                    val newHits = it.hits + index
                    var newSunk = it.sunk
                    if (newHits.containsAll(locations)) {
                        shipWasSunk = true
                        newSunk += ship
                    }
                    it.copy(
                        hits = newHits,
                        sunk = newSunk
                    )
                }
                val shipSunk = if (shipWasSunk) shipAtIndex(index) else null
                return Field.ShotResult(true, shipSunk)
            }
        }

        _fieldState.update {
            it.copy(
                misses = it.misses + index
            )
        }
        return Field.ShotResult(false, null)
    }

    private fun shipAtIndex(index: FieldIndex): Ship? {
        for (ship in Ship.entries) {
            if (fieldState.value.shipLocations[ship]?.contains(index) == true) {
                return ship
            }
        }
        return null
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

    class FactoryImpl : Field.Factory {
        override fun create(configuration: Configuration): Field {
            return OceanField(configuration)
        }
    }
}
