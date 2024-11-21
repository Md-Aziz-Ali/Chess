package com.example.chess

import android.content.Context
import android.widget.GridLayout

class ChessGame(private val context: Context, var time: Int, var isOnline: Boolean,
                var name: String, var receiverId: String) {
    // Declare gameState and chessBoard as properties
    private lateinit var gameState: GameState
    private lateinit var chessBoard: ChessBoard

    fun startGame(gridLayout: GridLayout) {
        // Initialize gameState and chessBoard
        gameState = GameState()
        if(isOnline) {
            var chessOnlineBoard = ChessOnlineBoard(context, gameState, name, time, receiverId)
            // Set up the chessboard UI and initialize the pieces
            chessOnlineBoard.updateUndoRedoButtons(canUndo = true, canRedo = true)
            chessOnlineBoard.startTheGame(gridLayout) // Pass gridLayout as a parameter
        }
        else {
            chessBoard = ChessBoard(context, gameState, time)

            // Set up the chessboard UI and initialize the pieces
            chessBoard.updateUndoRedoButtons(canUndo = true, canRedo = true)
            chessBoard.startTheGame(gridLayout) // Pass gridLayout as a parameter
        }
    }
}