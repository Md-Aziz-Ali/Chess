package com.example.chess

import android.content.Context
import android.widget.GridLayout

class ChessGame(private val context: Context) {

    private val gameState = GameState()  // Holds the game state (board state)
    private val chessBoard = ChessBoard(context, gameState)  // Handles UI and places pieces

    fun startGame(gridLayout: GridLayout) {
        // Set up the chessboard UI and initialize the pieces
        chessBoard.startTheGame(gridLayout)
    }
}