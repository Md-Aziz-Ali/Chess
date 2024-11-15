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
        gameState.redoStack.add(move)

        var piece = move.capturedPiece
        var startPosition = move.startPosition
        val endPosition = move.endPosition

        var rookStartPosition = move.rookStartPosition
        var rookEndPosition = move.rookEndPosition

        var isCastle = move.isCastle
        var isEnPassant = move.isEnPassant
        var tookOtherPiece = move.tookOtherPiece
        var tookPiecePosition = move.tookPosition
        var pawnPosition = move.pawnPosition

//        Toast.makeText(context, "${fromRow} ${fromCol} ${toRow} ${toCol}", Toast.LENGTH_LONG).show()

        // Get the ImageViews for the source and destination squares
//        val sourceSquare = gridLayout.getChildAt(fromRow * 8 + fromCol) as ImageView
//        val destinationSquare = gridLayout.getChildAt(toRow * 8 + toCol) as ImageView
//
//        // Clear the source square's image (empty the square)
//        destinationSquare.setImageDrawable(null)
////        resetBoard()
//
//        // Set the image for the destination square
////        destinationSquare.setImageDrawable(null)
//        sourceSquare.setImageResource(gameState.getPieceDrawable(gameState.board[toRow][toCol]))

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
                gameState.board[endPosition.first][endPosition.second] = piece
            }
        }


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
        val move = gameState.redoStack.removeAt(gameState.redoStack.lastIndex)
        gameState.undoStack.add(move)

        var piece = move.capturedPiece
        var startPosition = move.startPosition
        val endPosition = move.endPosition

        var rookStartPosition = move.rookStartPosition
        var rookEndPosition = move.rookEndPosition

        var isCastle = move.isCastle
        var isEnPassant = move.isEnPassant
        var tookOtherPiece = move.tookOtherPiece
        var tookPiecePosition = move.tookPosition
        var pawnPosition = move.pawnPosition

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
        }
        gameState.switchTurn()
    }
}