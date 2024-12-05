package com.lordinatec.battleship.gameplay.viewmodel

/**
 * Exception for when it's not the player's turn.
 */
class WrongTurnException : Exception("It's not your turn!")

/**
 * Exception for when the game has already started.
 */
class GameAlreadyStartedException : Exception("Game has already started!")

/**
 * Exception for when the game has not started or has ended.
 */
class GameNotActiveException : Exception("Game is not active!")

/**
 * Exception for when a given location has already been shot at.
 */
class AlreadyShotException : Exception("This location has already been shot at!")

/**
 * Exception for when ships have not been placed.
 */
class ShipsNotPlacedException : Exception("Ships have not been placed!")
