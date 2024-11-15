package com.example.chess

class GameState {
    // A simple 8x8 board initialized with pieces
    val board: Array<Array<String>> = Array(8) { Array(8) { "" } }
    var isCheckMate = false
    var winner = ""

    // Initialize the stacks for undo and redo operations
    val undoStack = mutableListOf<MutableList<Move>>()
    val redoStack = mutableListOf<MutableList<Move>>()

    var previousMoves: MutableList<Move> = mutableListOf()

    // Variable to track the current turn
    var currentTurn: String = "w"  // "w" for White, "b" for Black
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

    // Switch turns after a valid move
    fun switchTurn() {
        isWhiteTurn = !isWhiteTurn
    }
}
