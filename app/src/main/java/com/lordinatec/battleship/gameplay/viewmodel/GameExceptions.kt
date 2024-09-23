package com.lordinatec.battleship.gameplay.viewmodel

class WrongTurnException : Exception("It's not your turn!")
class GameAlreadyStartedException : Exception("Game has already started!")
class GameNotActiveException : Exception("Game is not active!")
class AlreadyShotException : Exception("This location has already been shot at!")
class ShipsNotPlacedException : Exception("Ships have not been placed!")
