package com.lordinatec.battleship.gameplay

/**
 * Data class representing the configuration of a field in the game.
 *
 * @property rows The number of rows in the field.
 * @property columns The number of columns in the field.
 *
 * @constructor Creates a new configuration with the given number of rows and columns.
 */
data class Configuration(val rows: Int, val columns: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Configuration) return false

        if (rows != other.rows) return false
        if (columns != other.columns) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + columns
        return result
    }
}
