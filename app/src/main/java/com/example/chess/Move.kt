package com.example.chess

data class Move(
    val capturedPiece: String,
    val startPosition: Pair<Int, Int>, // Starting position (row, col)
    val endPosition: Pair<Int, Int>,   // Ending position (row, col)
    val isCastle: Boolean,      // If true, it's a castling move, else it's a regular move
    val rookStartPosition: Pair<Int, Int>,
    val rookEndPosition: Pair<Int, Int>,

    val isEnPassant: Boolean,
    val pawnPosition: Pair<Int, Int>,

    val tookOtherPiece: String,
    val tookPosition: Pair<Int, Int>
)
