package com.example.chess

import android.os.CountDownTimer

class GameState {
    // A simple 8x8 board initialized with pieces
    val board: Array<Array<String>> = Array(8) { Array(8) { "" } }
    var isCheckMate = false
    var isDraw = false
    var winner = ""

    var whiteTimeRemaining: Long = 60000L // 1 minute in milliseconds
    var blackTimeRemaining: Long = 60000L// 1 minute in milliseconds
    var noTimeLimit = false


    // Initialize the stacks for undo and redo operations
//    var undoStack = mutableListOf<MutableList<Move>>()
//    var redoStack = mutableListOf<MutableList<Move>>()

    var undoStack = mutableListOf<Move>()  // Empty list of Move objects
    var redoStack = mutableListOf<Move>()  // Empty list of Move objects

//    var previousMoves: MutableList<Move> = mutableListOf()
    lateinit var previousMoves: Move
    var zeroMoves = true

    // Variable to track the current turn
//    var currentTurn: String = "w"  // "w" for White, "b" for Black
    var isWhiteTurn = true

    var whiteKingHasMoved = false
    var blackKingHasMoved = false
    var whiteLeftRookHasMoved = false
    var whiteRightRookHasMoved = false
    var blackLeftRookHasMoved = false
    var blackRightRookHasMoved = false

    init {
        // Initial positions of the pieces on the chessboard
        // Pawns
        board[6] = Array(8) { "wP" }  // White Pawns
        board[1] = Array(8) { "bP" }  // Black Pawns

        // Rooks
        board[7][0] = "wR"; board[7][7] = "wR"  // White Rooks
        board[0][0] = "bR"; board[0][7] = "bR"  // Black Rooks

        // Knights
        board[7][1] = "wN"; board[7][6] = "wN"  // White Knights
        board[0][1] = "bN"; board[0][6] = "bN"  // Black Knights

        // Bishops
        board[7][2] = "wB"; board[7][5] = "wB"  // White Bishops
        board[0][2] = "bB"; board[0][5] = "bB"  // Black Bishops

        // Queens
        board[7][3] = "wQ"  // White Queen
        board[0][3] = "bQ"  // Black Queen

        // Kings
        board[7][4] = "wK"  // White King
        board[0][4] = "bK"  // Black King
    }

    // Checks if it's the right player's turn to move
    fun isValidTurn(piece: String): Boolean {
        return if (isWhiteTurn && piece.startsWith("w")) {
            true
        } else if (!isWhiteTurn && piece.startsWith("b")) {
            true
        } else {
            false
        }
    }

    // Function to map the piece identifier to the corresponding drawable resource
    fun getPieceDrawable(piece: String): Int {
        return when (piece) {
            "wP" -> R.drawable.pawn_white
            "wR" -> R.drawable.rook_white
            "wN" -> R.drawable.knight_white
            "wB" -> R.drawable.bishop_white
            "wQ" -> R.drawable.queen_white
            "wK" -> R.drawable.king_white
            "bP" -> R.drawable.pawn_black
            "bR" -> R.drawable.rook_black
            "bN" -> R.drawable.knight_black
            "bB" -> R.drawable.bishop_black
            "bQ" -> R.drawable.queen_black
            "bK" -> R.drawable.king_black
            else -> 0
        }
    }

    // Switch turns after a valid move
    fun switchTurn() {
        isWhiteTurn = !isWhiteTurn
    }
}
