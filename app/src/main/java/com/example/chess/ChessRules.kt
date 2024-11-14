package com.example.chess

import android.content.ContentValues.TAG
import android.util.Log

//import GameState

class ChessRules(private val gameState: GameState) {
    private var isWhiteTurn: Boolean = true  // White moves first

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

    fun reset() {
        Log.d(TAG, "resetBoard: ChessRules")
        for (row in 0 until 8) {
            var rowString = ""
            for (col in 0 until 8) {
                val piece = gameState.board[row][col]
                rowString += if (piece.isNotEmpty()) "$piece " else "- "  // Use "-" for empty squares
            }
            Log.d(TAG, "resetBoard: Row $row: $rowString")
        }
    }

    // Function to check if a king will in check
    fun willKingBeInCheck(playerColor: String, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        var boardCopy = copyBoard(gameState.board)

        // en passant move
        if(boardCopy[fromRow][fromCol].endsWith("P") && boardCopy[toRow][toCol].isEmpty()) {
            boardCopy[toRow][toCol] = boardCopy[fromRow][fromCol]
            boardCopy[fromRow][fromCol] = ""
            boardCopy[fromRow][toCol] = ""
        }
        else {
            boardCopy[toRow][toCol] = boardCopy[fromRow][fromCol]
            boardCopy[fromRow][fromCol] = ""
        }

        return isKingInCheck(playerColor, boardCopy)
    }

    // Function to check if a king is in check
    fun isKingInCheck(playerColor: String, boardCopy: Array<Array<String>>): Boolean {

        val opponentColor = if (playerColor == "w") "b" else "w"



        // Find the position of the king of the given player color
        val kingPosition = findKingPosition(playerColor, boardCopy)

        // Check if any of the opponent's pieces can attack the king
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val piece = boardCopy[row][col]
                // Only check opponent's pieces
                if (piece.isNotEmpty() && piece[0].toString() == opponentColor) {
                    when (piece[1]) {
                        'P' -> if (canPawnAttack(row, col, kingPosition, opponentColor, boardCopy)) return true
                        'N' -> if (canKnightAttack(row, col, kingPosition, opponentColor, boardCopy)) return true
                        'B' -> if (canBishopAttack(row, col, kingPosition, opponentColor, boardCopy)) return true
                        'R' -> if (canRookAttack(row, col, kingPosition, opponentColor, boardCopy)) return true
                        'Q' -> if (canQueenAttack(row, col, kingPosition, opponentColor, boardCopy)) return true
                        'K' -> if (canKingAttack(row, col, kingPosition, opponentColor, boardCopy)) return true
                    }
                }
            }
        }
        return false
    }

    // Function to check if the square is attacked
    fun isSquareAttacked(squareRow: Int, squareCol: Int, playerColor: String): Boolean {

        val opponentColor = if (playerColor == "w") "b" else "w"
        val boardCopy = copyBoard(gameState.board)



        var squarePosition = Pair(squareRow, squareCol)

        // Check if any of the opponent's pieces can attack the king
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val piece = boardCopy[row][col]
                // Only check opponent's pieces
                if (piece.isNotEmpty() && piece[0].toString() == opponentColor) {
                    when (piece[1]) {
                        'P' -> if (canPawnAttack(row, col, squarePosition, opponentColor, boardCopy)) return true
                        'N' -> if (canKnightAttack(row, col, squarePosition, opponentColor, boardCopy)) return true
                        'B' -> if (canBishopAttack(row, col, squarePosition, opponentColor, boardCopy)) return true
                        'R' -> if (canRookAttack(row, col, squarePosition, opponentColor, boardCopy)) return true
                        'Q' -> if (canQueenAttack(row, col, squarePosition, opponentColor, boardCopy)) return true
                        'K' -> if (canKingAttack(row, col, squarePosition, opponentColor, boardCopy)) return true
                    }
                }
            }
        }
        return false
    }

    // Helper function to check if a pawn can attack the king
    fun canPawnAttack(row: Int, col: Int, kingPosition: Pair<Int, Int>, opponentColor: String, boardCopy: Array<Array<String>>): Boolean {
        val (kingRow, kingCol) = kingPosition
        val direction = if (opponentColor == "w") -1 else 1  // White pawns move up, black pawns move down
        return (kingRow == row + direction) && (kingCol == col + 1 || kingCol == col - 1)
    }

    // Helper function to check if a knight can attack the king
    fun canKnightAttack(row: Int, col: Int, kingPosition: Pair<Int, Int>, opponentColor: String, boardCopy: Array<Array<String>>): Boolean {
        val (kingRow, kingCol) = kingPosition
        val knightMoves = listOf(
            Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
        )
        return knightMoves.any { (dRow, dCol) -> kingRow == row + dRow && kingCol == col + dCol }
    }

    // Helper function to check if a bishop can attack the king
    fun canBishopAttack(row: Int, col: Int, kingPosition: Pair<Int, Int>, opponentColor: String, boardCopy: Array<Array<String>>): Boolean {
        val (kingRow, kingCol) = kingPosition
        if (Math.abs(kingRow - row) != Math.abs(kingCol - col)) return false  // Not on the same diagonal

        val rowDirection = if (kingRow > row) 1 else -1
        val colDirection = if (kingCol > col) 1 else -1
        var currentRow = row + rowDirection
        var currentCol = col + colDirection

        while (currentRow != kingRow && currentCol != kingCol) {
            if (boardCopy[currentRow][currentCol].isNotEmpty()) return false  // Path is blocked
            currentRow += rowDirection
            currentCol += colDirection
        }
        return true
    }

    // Helper function to check if a rook can attack the king
    fun canRookAttack(row: Int, col: Int, kingPosition: Pair<Int, Int>, opponentColor: String, boardCopy: Array<Array<String>>): Boolean {
        val (kingRow, kingCol) = kingPosition
        if (kingRow != row && kingCol != col) return false  // Not on the same row or column

        val rowDirection = if (kingRow > row) 1 else if (kingRow < row) -1 else 0
        val colDirection = if (kingCol > col) 1 else if (kingCol < col) -1 else 0
        var currentRow = row + rowDirection
        var currentCol = col + colDirection

        while (currentRow != kingRow || currentCol != kingCol) {
            if (boardCopy[currentRow][currentCol].isNotEmpty()) return false  // Path is blocked
            currentRow += rowDirection
            currentCol += colDirection
        }
        return true
    }

    // Helper function to check if a queen can attack the king
    fun canQueenAttack(row: Int, col: Int, kingPosition: Pair<Int, Int>, opponentColor: String, boardCopy: Array<Array<String>>): Boolean {
        return canRookAttack(row, col, kingPosition, opponentColor, boardCopy) || canBishopAttack(row, col, kingPosition, opponentColor, boardCopy)
    }

    // Helper function to check if a king can attack the king (king adjacency)
    fun canKingAttack(row: Int, col: Int, kingPosition: Pair<Int, Int>, opponentColor: String, boardCopy: Array<Array<String>>): Boolean {
        val (kingRow, kingCol) = kingPosition
        return Math.abs(kingRow - row) <= 1 && Math.abs(kingCol - col) <= 1
    }



    fun copyBoard(originalBoard: Array<Array<String>>): Array<Array<String>> {
        return Array(8) { row ->
            Array(8) { col ->
                originalBoard[row][col]  // Copy each element individually
            }
        }
    }

    // Helper function to find the king's position
    private fun findKingPosition(color: String, boardCopy: Array<Array<String>>): Pair<Int, Int> {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val piece = boardCopy[row][col]
                if (piece == "${color}K") {  // Check for the king piece (e.g., "wK" or "bK")
                    return Pair(row, col)
                }
            }
        }
        throw IllegalStateException("King not found!")
    }

    // Returns the current player for display purposes
    fun currentPlayer(): String {
        return if (isWhiteTurn) "White" else "Black"
    }
}