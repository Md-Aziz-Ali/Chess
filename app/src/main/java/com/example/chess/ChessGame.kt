package com.example.chess

import android.content.Context
import android.widget.GridLayout

class ChessGame(private val context: Context, var time: Int) {
    // Declare gameState and chessBoard as properties
    private lateinit var gameState: GameState
    private lateinit var chessBoard: ChessBoard

    fun startGame(gridLayout: GridLayout) {
        // Initialize gameState and chessBoard
        gameState = GameState()
        chessBoard = ChessBoard(context, gameState, time)

        // Set up the chessboard UI and initialize the pieces
        chessBoard.updateUndoRedoButtons(canUndo = true, canRedo = true)
        chessBoard.startTheGame(gridLayout) // Pass gridLayout as a parameter
    }
}