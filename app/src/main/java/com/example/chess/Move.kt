package com.example.chess

data class Move(
    val piece: String,
    val startPosition: Pair<Int, Int>, // Starting position (row, col)
    val endPosition: Pair<Int, Int>,   // Ending position (row, col)
    val isCastle: Boolean = false      // If true, it's a castling move, else it's a regular move
)
