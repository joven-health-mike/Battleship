package com.lordinatec.battleship.gameplay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AndroidField(override var configuration: Configuration) : Field {

    private val _fieldState = MutableStateFlow(FieldState())
    override val fieldState: StateFlow<FieldState>
        get() = _fieldState.asStateFlow()

    override fun reset(configuration: Configuration) {
        if (configuration != this.configuration) this.configuration = configuration
        _fieldState.update { FieldState() }
    }

    override fun shoot(index: FieldIndex): Hit {
        val fieldState = fieldState.value
        fieldState.shipLocations.forEach { (ship, locations) ->
            if (locations.contains(index)) {
                _fieldState.update {
                    it.copy(
                        hits = fieldState.hits + index,
                    )
                }
                if (fieldState.hits.containsAll(locations)) {
                    _fieldState.update {
                        it.copy(
                            sunk = fieldState.sunk + ship
                        )
                    }
                }
                return true
            }
        }
        return false
    }

    override fun placeShip(ship: Ship, location: List<FieldIndex>) {
        require(!fieldState.value.shipLocations.containsKey(ship))
        require(ship.length == location.size)
        _fieldState.update {
            it.copy(
                shipLocations = fieldState.value.shipLocations + (ship to location)
            )
        }
    }
}
