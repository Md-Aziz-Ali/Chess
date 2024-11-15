package com.example.chess

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import kotlin.coroutines.coroutineContext
import kotlin.math.log

class GenerateMoves(private val gameState: GameState) {
    var chessRules = ChessRules(gameState)

    fun reset() {
        Log.d(TAG, "resetBoard: GenerateMoves")
        for (row in 0 until 8) {
            var rowString = ""
            for (col in 0 until 8) {
                val piece = gameState.board[row][col]
                rowString += if (piece.isNotEmpty()) "$piece " else "- "  // Use "-" for empty squares
            }
            Log.d(TAG, "resetBoard: Row $row: $rowString")
        }
    }

    fun possibleMoves(row: Int, col: Int): MutableList<Pair<Boolean, Pair<Int, Int>>> {
        val moves = mutableListOf<Pair<Boolean, Pair<Int, Int>>>()
        val piece = gameState.board[row][col]

        // Determine possible moves based on piece type
        when (piece[1]) {
            'P' -> generatePawnMoves(row, col, moves, piece[0].toString())
            'R' -> generateRookMoves(row, col, moves, piece[0].toString())
            'N' -> generateKnightMoves(row, col, moves, piece[0].toString())
            'B' -> generateBishopMoves(row, col, moves, piece[0].toString())
            'Q' -> generateQueenMoves(row, col, moves, piece[0].toString())
            'K' -> generateKingMoves(row, col, moves, piece[0].toString())
        }

        return moves
    }

    fun checkEnPassantMove(row: Int, col: Int, playerColor: String): Boolean {
        var movedTo = gameState.previousMoves.endPosition
        var piece = gameState.board[movedTo.first][movedTo.second]
        var possible = gameState.previousMoves.endPosition.first == row && gameState.previousMoves.endPosition.second == col && piece.endsWith("P")

        // done opposite to find the start row of the pawn of the opponent
        var startRow = if(playerColor == "w") 1 else 6
//        possible = possible && gameState.previousMoves.startPosition.first == startRow && gameState.previousMoves.startPosition.second == col
        return possible
    }

    private fun generatePawnMoves(row: Int, col: Int, moves: MutableList<Pair<Boolean, Pair<Int, Int>>>, playerColor: String) {
        val direction = if (playerColor == "w") -1 else 1
        val startRow = if (playerColor == "w") 6 else 1  // White pawns start on row 6, black on row 1
        val targetRow = row + direction

        // For en passant move
//        if(true) {
        if(gameState.zeroMoves == false) {
            if(isWithinBounds(row, col + 1) && checkEnPassantMove(row, col + 1, playerColor)) {
                if(!chessRules.willKingBeInCheck(playerColor, row, col, targetRow, col + 1)) {
                    moves.add(Pair(true, Pair(targetRow, col + 1)))
                }
            }
            if(isWithinBounds(row, col - 1) && checkEnPassantMove(row, col - 1, playerColor)) {
                if(!chessRules.willKingBeInCheck(playerColor, row, col, targetRow, col - 1)) {
                    moves.add(Pair(true, Pair(targetRow, col - 1)))
                }
            }
        }

        // Single forward move
        if (isWithinBounds(targetRow, col) && gameState.board[targetRow][col].isEmpty()) {
            if (!chessRules.willKingBeInCheck(playerColor, row, col, targetRow, col)) {
                moves.add(Pair(false, Pair(targetRow, col)))
            }

            // Double forward move (only if on starting row and the square two steps ahead is empty)
            val doubleStepRow = row + 2 * direction
            if (row == startRow && gameState.board[doubleStepRow][col].isEmpty()) {
                if (!chessRules.willKingBeInCheck(playerColor, row, col, doubleStepRow, col)) {
                    moves.add(Pair(false, Pair(doubleStepRow, col)))
                }
            }
        }

        // Capture moves
        for (offset in listOf(-1, 1)) {
            val captureCol = col + offset
            if (isWithinBounds(targetRow, captureCol) && gameState.board[targetRow][captureCol].startsWith(opponentColor(playerColor))) {
                if (!chessRules.willKingBeInCheck(playerColor, row, col, targetRow, captureCol)) {
                    moves.add(Pair(false, Pair(targetRow, captureCol)))
                }
            }
        }
    }


    private fun generateRookMoves(row: Int, col: Int, moves: MutableList<Pair<Boolean, Pair<Int, Int>>>, playerColor: String) {
        val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0)) // Right, Left, Down, Up

        for ((dRow, dCol) in directions) {
            var newRow = row + dRow
            var newCol = col + dCol

            while (isWithinBounds(newRow, newCol)) {
                if (gameState.board[newRow][newCol].isEmpty()) {
                    if (!chessRules.willKingBeInCheck(playerColor, row, col, newRow, newCol)) {
                        moves.add(Pair(false, Pair(newRow, newCol)))
                    }
                } else if (gameState.board[newRow][newCol].startsWith(opponentColor(playerColor))) {
                    if (!chessRules.willKingBeInCheck(playerColor, row, col, newRow, newCol)) {
                        moves.add(Pair(false, Pair(newRow, newCol)))
                    }
                    break
                } else {
                    break
                }
                newRow += dRow
                newCol += dCol
            }
        }
    }

    private fun generateKnightMoves(row: Int, col: Int, moves: MutableList<Pair<Boolean, Pair<Int, Int>>>, playerColor: String) {
        val knightMoves = listOf(Pair(2, 1), Pair(2, -1), Pair(-2, 1), Pair(-2, -1), Pair(1, 2), Pair(1, -2), Pair(-1, 2), Pair(-1, -2))

        for ((dRow, dCol) in knightMoves) {
            val newRow = row + dRow
            val newCol = col + dCol
            if (isWithinBounds(newRow, newCol) && (gameState.board[newRow][newCol].isEmpty() || gameState.board[newRow][newCol].startsWith(opponentColor(playerColor)))) {
                if (!chessRules.willKingBeInCheck(playerColor, row, col, newRow, newCol)) {
                    moves.add(Pair(false, Pair(newRow, newCol)))
                }
            }
        }
    }

    private fun generateBishopMoves(row: Int, col: Int, moves: MutableList<Pair<Boolean, Pair<Int, Int>>>, playerColor: String) {
        val directions = listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1)) // Diagonal directions

        for ((dRow, dCol) in directions) {
            var newRow = row + dRow
            var newCol = col + dCol

            while (isWithinBounds(newRow, newCol)) {
                if (gameState.board[newRow][newCol].isEmpty()) {
                    if (!chessRules.willKingBeInCheck(playerColor, row, col, newRow, newCol)) {
                        moves.add(Pair(false, Pair(newRow, newCol)))
                    }
                } else if (gameState.board[newRow][newCol].startsWith(opponentColor(playerColor))) {
                    if (!chessRules.willKingBeInCheck(playerColor, row, col, newRow, newCol)) {
                        moves.add(Pair(false, Pair(newRow, newCol)))
                    }
                    break
                } else {
                    break
                }
                newRow += dRow
                newCol += dCol
            }
        }
    }

    private fun generateQueenMoves(row: Int, col: Int, moves: MutableList<Pair<Boolean, Pair<Int, Int>>>, playerColor: String) {
        generateRookMoves(row, col, moves, playerColor) // Queen combines rook moves
        generateBishopMoves(row, col, moves, playerColor) // and bishop moves
    }

    private fun generateKingMoves(row: Int, col: Int, moves: MutableList<Pair<Boolean, Pair<Int, Int>>>, playerColor: String) {
        val kingMoves = listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1), Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))

        for ((dRow, dCol) in kingMoves) {
            val newRow = row + dRow
            val newCol = col + dCol
            if (isWithinBounds(newRow, newCol) && (gameState.board[newRow][newCol].isEmpty() || gameState.board[newRow][newCol].startsWith(opponentColor(playerColor)))) {
                if (!chessRules.willKingBeInCheck(playerColor, row, col, newRow, newCol)) {
                    moves.add(Pair(false, Pair(newRow, newCol)))
                }
            }
        }
        generateCastlingMoves(playerColor, moves)
    }

    fun generateCastlingMoves(playerColor: String, moves: MutableList<Pair<Boolean, Pair<Int, Int>>>) {
        val kingRow = if (playerColor == "w") 7 else 0
        val rookColRight = 7
        val rookColLeft = 0
        val board = gameState.board

        // Check if the king and the corresponding rook have not moved
        if (playerColor == "w" && !gameState.whiteKingHasMoved) {
            // Kingside castling for white
            if (!gameState.whiteRightRookHasMoved &&
                gameState.board[kingRow][5].isEmpty() && gameState.board[kingRow][6].isEmpty() &&
                !chessRules.isKingInCheck("w", board) && !chessRules.isSquareAttacked(kingRow, 5, "w") && !chessRules.isSquareAttacked(kingRow, 6, "w")) {
                moves.add(Pair(true,Pair(kingRow, 6)))  // Kingside castling move
            }
            // Queenside castling for white
            if (!gameState.whiteLeftRookHasMoved &&
                gameState.board[kingRow][1].isEmpty() && gameState.board[kingRow][2].isEmpty() && gameState.board[kingRow][3].isEmpty() &&
                !chessRules.isKingInCheck("w", board) && !chessRules.isSquareAttacked(kingRow,0, "w") && !chessRules.isSquareAttacked(kingRow,3, "w")) {
                moves.add(Pair(true, Pair(kingRow, 2)))  // Queenside castling move
            }
        }

        if (playerColor == "b" && !gameState.blackKingHasMoved) {
            // Kingside castling for black
            if (!gameState.blackRightRookHasMoved &&
                gameState.board[kingRow][5].isEmpty() && gameState.board[kingRow][6].isEmpty() &&
                !chessRules.isKingInCheck("b", board) && !chessRules.isSquareAttacked(kingRow, 5, "b") && !chessRules.isSquareAttacked(kingRow, 6, "b")) {
                moves.add(Pair(true, Pair(kingRow, 6)))  // Kingside castling move
            }
            // Queenside castling for black
            if (!gameState.blackLeftRookHasMoved &&
                gameState.board[kingRow][1].isEmpty() && gameState.board[kingRow][2].isEmpty() && gameState.board[kingRow][3].isEmpty() &&
                !chessRules.isKingInCheck("b", board) && !chessRules.isSquareAttacked(kingRow, 2, "b") && !chessRules.isSquareAttacked(kingRow, 3, "b")) {
                moves.add(Pair(true, Pair(kingRow, 2)))  // Queenside castling move
            }
        }
    }

    // Utility function to check if a position is within board boundaries
    private fun isWithinBounds(row: Int, col: Int): Boolean {
        return row in 0..7 && col in 0..7
    }

    // Utility function to get the opponent's color
    private fun opponentColor(playerColor: String): String {
        return if (playerColor == "w") "b" else "w"
    }

}