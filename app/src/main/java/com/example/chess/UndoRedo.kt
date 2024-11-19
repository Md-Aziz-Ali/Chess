package com.example.chess

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast

class UndoRedo(private val context: Context, var gameState: GameState) {
    fun undo(gridLayout: GridLayout) {

        if(gameState.undoStack.isEmpty())
            return
        var move = gameState.undoStack.removeAt(gameState.undoStack.lastIndex)
        if(gameState.undoStack.isEmpty())
            gameState.zeroMoves = true
        gameState.redoStack.add(move)

        var piece = move.piece
        var startPosition = move.startPosition
        val endPosition = move.endPosition

        var rookStartPosition = move.rookStartPosition
        var rookEndPosition = move.rookEndPosition

        var isCastle = move.isCastle
        var isEnPassant = move.isEnPassant
        var tookOtherPiece = move.tookOtherPiece
        var tookPiecePosition = move.tookPosition
        var pawnPosition = move.pawnPosition
        var isUpgraded = move.isUpgraded

        var whiteLeftRookMoved = move.whiteLeftRookMoved
        var whiteRightRookMoved = move.whiteRightRookMoved
        var whiteKingMoved = move.whiteKingMoved
        var blackLeftRookMoved = move.blackLeftRookMoved
        var blackRightRookMoved = move.blackRightRookMoved
        var blackKingMoved = move.blackKingMoved


        if(isCastle) {
            gameState.board[startPosition.first][startPosition.second] = gameState.board[endPosition.first][endPosition.second]
            gameState.board[endPosition.first][endPosition.second] = ""

            gameState.board[rookStartPosition.first][rookStartPosition.second] = gameState.board[rookEndPosition.first][rookEndPosition.second]
            gameState.board[rookEndPosition.first][rookEndPosition.second] = ""
        }
        else if(isEnPassant) {
            gameState.board[startPosition.first][startPosition.second] = gameState.board[endPosition.first][endPosition.second]
            gameState.board[endPosition.first][endPosition.second] = ""

            gameState.board[pawnPosition.first][pawnPosition.second] = if (gameState.isWhiteTurn) "wP" else "bP"
        }
        else {
            gameState.board[startPosition.first][startPosition.second] = gameState.board[endPosition.first][endPosition.second]
            gameState.board[endPosition.first][endPosition.second] = ""
            if(tookOtherPiece.isNotEmpty()) {
                gameState.board[endPosition.first][endPosition.second] = tookOtherPiece
            }
            if(isUpgraded.isNotEmpty()) {
                gameState.board[startPosition.first][startPosition.second] = if(gameState.isWhiteTurn) "bP" else "wP"
            }
        }

        if(whiteKingMoved)
            gameState.whiteKingHasMoved = false
        if(whiteLeftRookMoved)
            gameState.whiteLeftRookHasMoved = false
        if(whiteRightRookMoved)
            gameState.whiteRightRookHasMoved = false
        if(blackKingMoved)
            gameState.blackKingHasMoved = false
        if(blackLeftRookMoved)
            gameState.blackLeftRookHasMoved = false
        if(blackRightRookMoved)
            gameState.blackRightRookHasMoved = false

        if(gameState.undoStack.isNotEmpty())
            gameState.previousMoves = gameState.undoStack.last()
        else
            gameState.zeroMoves = true
        gameState.switchTurn()
//        print()
    }

    fun print() {
        for (row in 0 until 8) {
            var rowString = ""
            for (col in 0 until 8) {
                val piece = gameState.board[row][col]
                rowString += if (piece.isNotEmpty()) "$piece " else "- "  // Use "-" for empty squares
            }
            Log.d(TAG, "resetBoard: Row $row: $rowString")
        }
    }

    fun redo(gridLayout: GridLayout) {
        if(gameState.redoStack.isEmpty())
            return
        gameState.zeroMoves = false
        val move = gameState.redoStack.removeAt(gameState.redoStack.lastIndex)
        gameState.undoStack.add(move)

        var piece = move.piece
        var startPosition = move.startPosition
        val endPosition = move.endPosition

        var rookStartPosition = move.rookStartPosition
        var rookEndPosition = move.rookEndPosition

        var isCastle = move.isCastle
        var isEnPassant = move.isEnPassant
        var tookOtherPiece = move.tookOtherPiece
        var tookPiecePosition = move.tookPosition
        var pawnPosition = move.pawnPosition
        var isUpgraded = move.isUpgraded

        var whiteLeftRookMoved = move.whiteLeftRookMoved
        var whiteRightRookMoved = move.whiteRightRookMoved
        var whiteKingMoved = move.whiteKingMoved
        var blackLeftRookMoved = move.blackLeftRookMoved
        var blackRightRookMoved = move.blackRightRookMoved
        var blackKingMoved = move.blackKingMoved

        if(isCastle) {
            gameState.board[endPosition.first][endPosition.second] = gameState.board[startPosition.first][startPosition.second]
            gameState.board[startPosition.first][startPosition.second] = ""

            gameState.board[rookEndPosition.first][rookEndPosition.second] = gameState.board[rookStartPosition.first][rookStartPosition.second]
            gameState.board[rookStartPosition.first][rookStartPosition.second] = ""
        }
        else if(isEnPassant) {
            gameState.board[endPosition.first][endPosition.second] = gameState.board[startPosition.first][startPosition.second]
            gameState.board[startPosition.first][startPosition.second] = ""

            gameState.board[pawnPosition.first][pawnPosition.second] = ""
        }
        else {
            gameState.board[endPosition.first][endPosition.second] = gameState.board[startPosition.first][startPosition.second]
            gameState.board[startPosition.first][startPosition.second] = ""
//            if(tookOtherPiece.isNotEmpty()) {
//                gameState.board[endPosition.first][endPosition.second] = piece
//            }
            if(isUpgraded.isNotEmpty()) {
                gameState.board[endPosition.first][endPosition.second] = isUpgraded
            }
        }

        if(whiteKingMoved)
            gameState.whiteKingHasMoved = true
        if(whiteLeftRookMoved)
            gameState.whiteLeftRookHasMoved = true
        if(whiteRightRookMoved)
            gameState.whiteRightRookHasMoved = true
        if(blackKingMoved)
            gameState.blackKingHasMoved = true
        if(blackLeftRookMoved)
            gameState.blackLeftRookHasMoved = true
        if(blackRightRookMoved)
            gameState.blackRightRookHasMoved = true



        gameState.previousMoves = move
        gameState.switchTurn()
    }
}
