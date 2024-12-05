/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.battleship.gameplay.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ConfigurationTest {

    @Test
    fun testEquals() = runTest {
        val config1 = Configuration(10, 10)
        val config2 = Configuration(10, 10)
        assertEquals(config1, config2)
    }

    @Test
    fun testNotEqualsRows() = runTest {
        val config1 = Configuration(10, 10)
        val config2 = Configuration(8, 10)
        assertNotEquals(config1, config2)
    }

    @Test
    fun testNotEqualsCols() = runTest {
        val config1 = Configuration(10, 10)
        val config2 = Configuration(10, 8)
        assertNotEquals(config1, config2)
    }

    @Test
    fun testNotEqualsRowsAndCols() = runTest {
        val config1 = Configuration(8, 10)
        val config2 = Configuration(10, 8)
        assertNotEquals(config1, config2)
    }
}
