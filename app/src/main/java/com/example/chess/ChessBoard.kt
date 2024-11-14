package com.example.chess

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.currentCoroutineContext
import kotlin.math.log

class ChessBoard(private val context: Context, var gameState: GameState) {

    val highlight = mutableListOf<Int>()
    var generateMoves = GenerateMoves(gameState)
    val circleViews = mutableListOf<ImageView>() // List to store circle ImageViews
    var moves = mutableListOf<Pair<Boolean,Pair<Int, Int>>>()

    lateinit var gridLayout: GridLayout

//    // Declare previousMoves as a mutable list of Move objects
//    val previousMoes: MutableList<Move> = mutableListOf()


    // Array to keep track of highlighted squares
    private val highlightedSquares = mutableListOf<ImageView>()

    var selectedPiece: Pair<Int, Int>? = null  // Track the selected piece's position

    var chessRules = ChessRules(gameState)

    var currSourceSquare: ImageView? = null

    val HIGHLIGHT_SOURCE = Color.parseColor("#FFD700")  // Gold
    val HIGHLIGHT_DESTINATION = Color.parseColor("#00FF00")  // Green


    fun setupBoard(gridLayout: GridLayout) {
        this.gridLayout = gridLayout
        gridLayout.rowCount = 8
        gridLayout.columnCount = 8

        // Calculate square size based on the GridLayout width
        gridLayout.post {
            val squareSize = gridLayout.width / 8

            // Loop through the game state and place the pieces
            for (row in 0 until 8) {
                for (col in 0 until 8) {
                    val square = ImageView(context)
                    square.setBackgroundColor(if ((row + col) % 2 == 0) LIGHT_COLOR else DARK_COLOR)

                    // Set layout parameters for each square
                    val params = GridLayout.LayoutParams()
                    params.width = squareSize
                    params.height = squareSize
                    params.rowSpec = GridLayout.spec(row)
                    params.columnSpec = GridLayout.spec(col)
                    square.layoutParams = params
                    gridLayout.addView(square)

                    // Initialize the piece on this square
                    val piece = gameState.board[row][col]
                    if (piece.isNotEmpty()) {
                        square.setImageResource(getPieceDrawable(piece))
                    }

                    // Handle click event for each square
                    square.setOnClickListener {
                        onSquareClicked(row, col, square, gridLayout)
                    }
                }
            }
        }
    }

    fun onSquareClicked(row: Int, col: Int, square: ImageView, gridLayout: GridLayout) {
        if(selectedPiece != null) {
            val currPiece = gameState.board[row][col]
            val (prevSelectedRow, prevSelectedCol) = selectedPiece!!
            val prevPiece = gameState.board[prevSelectedRow][prevSelectedCol]
            if(currPiece.isNotEmpty() && prevPiece.first() == currPiece.first()) {
                removePossibleMoves(gridLayout)
                selectedPiece = null
                currSourceSquare?.setBackgroundColor(if ((prevSelectedRow + prevSelectedCol) % 2 == 0) LIGHT_COLOR else DARK_COLOR)
            }
        }
        if (selectedPiece == null) {
            // Select the piece at this position if it's not empty and it's the correct turn
            val piece = gameState.board[row][col]
            if (piece.isNotEmpty() && gameState.isValidTurn(piece)) {
                selectedPiece = Pair(row, col)

                // Highlight the selected (source) square
                square.setBackgroundColor(HIGHLIGHT_SOURCE)

                // Store the source square for later resetting
                currSourceSquare = square
                moves = generateMoves.possibleMoves(row, col)
                showPossibleMoves(gridLayout)
            }
        } else {
            val position1 = Pair(true,Pair(row, col))
            val position2 = Pair(false, Pair(row, col))
            if (moves.contains(position1) || moves.contains(position2)) {
                // The position (targetRow, targetCol) is in the moves array
            } else {
                // The position is not in the moves array
                return
            }
            // Move the selected piece to the new position
            val (selectedRow, selectedCol) = selectedPiece!!
            currSourceSquare?.setBackgroundColor(if ((selectedRow + selectedCol) % 2 == 0) LIGHT_COLOR else DARK_COLOR)

            // Make the move
            makeMove(gridLayout, row, col, selectedRow, selectedCol)

            // Switch turns after a valid move
            gameState.switchTurn()

            // Clear the selection
            selectedPiece = null
            removePossibleMoves(gridLayout)
        }
    }

    // Function to map the piece identifier to the corresponding drawable resource
    private fun getPieceDrawable(piece: String): Int {
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

    fun removePossibleMoves(gridLayout: GridLayout) {
        // Iterate through the stored circle views and remove them
        for (circle in circleViews) {
            gridLayout.removeView(circle)
        }
        // Clear the list after removal
        circleViews.clear()
    }


    fun showPossibleMoves(gridLayout: GridLayout) {
        for (move in moves) {
            val row = move.second.first
            val col = move.second.second

            // Create a new ImageView for the circle
            val circle = ImageView(context)
            circle.setImageResource(R.drawable.circle)  // Set the circle drawable you created

            // Set layout parameters for the circle to position it on the grid
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(row)
            params.columnSpec = GridLayout.spec(col)
            circle.layoutParams = params

            // Center the ImageView within its cell
            params.setGravity(Gravity.CENTER)

            // Add the circle to the GridLayout
            gridLayout.addView(circle)

            // Store the circle ImageView in the list
            circleViews.add(circle)
        }
    }

    // Function to highlight the source and destination squares
    fun highlightMoves(gridLayout: GridLayout, row1: Int, col1: Int, row2: Int, col2: Int) {
        highlight.clear()
        highlight.add(row1)
        highlight.add(col1)
        highlight.add(row2)
        highlight.add(col2)
        val sourceSquare = gridLayout.getChildAt(row1 * 8 + col1) as ImageView
        val destinationSquare = gridLayout.getChildAt(row2 * 8 + col2) as ImageView

        // Highlight the source square
        sourceSquare.setBackgroundColor(HIGHLIGHT_SOURCE)  // HIGHLIGHT_SOURCE is a color for highlighting the source
        // Highlight the destination square
        destinationSquare.setBackgroundColor(HIGHLIGHT_DESTINATION)  // HIGHLIGHT_DESTINATION is a color for destination
    }

    // Function to remove the highlight from the previously highlighted squares
    fun removeHighlightedMoves(gridLayout: GridLayout) {
        if (highlight.isNotEmpty()) {
            // Assuming highlight array has 4 values: [sourceRow, sourceCol, destinationRow, destinationCol]
            val sourceRow = highlight[0]
            val sourceCol = highlight[1]
            val destinationRow = highlight[2]
            val destinationCol = highlight[3]

            // Get the source and destination squares from GridLayout
            val sourceSquare = gridLayout.getChildAt(sourceRow * 8 + sourceCol) as ImageView
            val destinationSquare = gridLayout.getChildAt(destinationRow * 8 + destinationCol) as ImageView

            // Reset the background color to default (alternating light/dark color)
            sourceSquare.setBackgroundColor(if ((sourceRow + sourceCol) % 2 == 0) LIGHT_COLOR else DARK_COLOR)
            destinationSquare.setBackgroundColor(if ((destinationRow + destinationCol) % 2 == 0) LIGHT_COLOR else DARK_COLOR)
        }
    }

    fun makeMove(gridLayout: GridLayout, row: Int, col: Int, selectedRow: Int, selectedCol: Int) {
        val previousMovesCopy = gameState.previousMoves.map { it.copy() }.toTypedArray()
        // Clear previous moves before adding the new one
        gameState.previousMoves.clear()
        removeHighlightedMoves(gridLayout)

        // Check if the move involves a king or a rook
        val movedPiece = gameState.board[selectedRow][selectedCol]


        // Check if the moved piece is a king (ends with "K")
        if (movedPiece.endsWith("K")) {
            if (movedPiece.startsWith("w")) {  // White King
                gameState.whiteKingHasMoved = true
            } else if (movedPiece.startsWith("b")) {  // Black King
                gameState.blackKingHasMoved = true
            }
        }

        // Check if the moved piece is a rook (ends with "R")
        if (movedPiece.endsWith("R")) {
            if (movedPiece.startsWith("w")) {  // White Rook
                if (selectedCol == 0) {  // Left Rook
                    gameState.whiteLeftRookHasMoved = true
                } else if (selectedCol == 7) {  // Right Rook
                    gameState.whiteRightRookHasMoved = true
                }
            } else if (movedPiece.startsWith("b")) {  // Black Rook
                if (selectedCol == 0) {  // Left Rook
                    gameState.blackLeftRookHasMoved = true
                } else if (selectedCol == 7) {  // Right Rook
                    gameState.blackRightRookHasMoved = true
                }
            }
        }



        // Check if the move is a castling attempt
        if (gameState.board[selectedRow][selectedCol].endsWith("K") && Math.abs(col - selectedCol) == 2) {
            // Call the castling function if the move meets castling conditions
            performCastling(selectedRow, selectedCol, col)

            // Add the castling move to the array
            gameState.previousMoves.add(
                Move(
                    piece = gameState.board[selectedRow][col],
                    startPosition = Pair(selectedRow, selectedCol),
                    endPosition = Pair(selectedRow, col),
                    isCastle = false  // Mark this as a castling move
                )
            )

            updateGridLayout(gridLayout, selectedRow, selectedCol, selectedRow, col)

            // Rook's move for castling
            val rookCol = if (col > selectedCol) 7 else 0
            val newRookCol = if (col > selectedCol) col - 1 else col + 1
            gameState.previousMoves.add(
                Move(
                    piece = gameState.board[selectedRow][newRookCol],
                    startPosition = Pair(selectedRow, rookCol),
                    endPosition = Pair(selectedRow, newRookCol),
                    isCastle = true  // Mark this as a castling move
                )
            )
            updateGridLayout(gridLayout, selectedRow, rookCol, selectedRow, newRookCol)
            highlightMoves(gridLayout, selectedRow, selectedCol,selectedRow, rookCol)

        }
        // check for en passant
        else if(movedPiece.endsWith("P") && gameState.board[row][col] == "" && col != selectedCol) {
            val removeRowPiece = previousMovesCopy[0].endPosition.first
            val removeColPiece = previousMovesCopy[0].endPosition.second

            val removePieceSquare = gridLayout.getChildAt(removeRowPiece * 8 + removeColPiece) as ImageView
            removePieceSquare.setImageDrawable(null)

            // move
            gameState.board[row][col] = gameState.board[selectedRow][selectedCol]
            gameState.board[selectedRow][selectedCol] = ""
            gameState.board[removeRowPiece][removeColPiece] = ""

            // add the move to the array
            gameState.previousMoves.add(
                Move(
                    piece = gameState.board[row][col],
                    startPosition = Pair(selectedRow, selectedCol),
                    endPosition = Pair(row, col),
                    isCastle = false  // Mark this as a regular move
                )
            )

            updateGridLayout(gridLayout, selectedRow, selectedCol, row, col)
        }
        else {
            // Regular move
            gameState.board[row][col] = gameState.board[selectedRow][selectedCol]
            gameState.board[selectedRow][selectedCol] = ""

            // Add the regular move to the array
            gameState.previousMoves.add(
                Move(
                    piece = gameState.board[row][col],
                    startPosition = Pair(selectedRow, selectedCol),
                    endPosition = Pair(row, col),
                    isCastle = false  // Mark this as a regular move
                )
            )
            updateGridLayout(gridLayout, selectedRow, selectedCol, row, col)
            highlightMoves(gridLayout, selectedRow, selectedCol, row, col)
        }
        if(isCheckMate(gameState.currentTurn)) {
            showWinnerDialog(context, gameState.winner)
        }
    }

    // Function to check checkmate
    fun isCheckMate(playerColor: String): Boolean {
        val opponentColor = if (playerColor == "w") "b" else "w"
        var boardCopy = chessRules.copyBoard(gameState.board)
        if(!chessRules.isKingInCheck(opponentColor, boardCopy))
            return false

        for(row in 0 until 8) {
            for(col in 0 until 8) {
                val piece = boardCopy[row][col]
                if(piece.isNotEmpty() && piece[0].toString() == opponentColor) {
                    val moves = generateMoves.possibleMoves(row, col)
                    if(moves.isNotEmpty())
                        return false
                }
            }
        }
        gameState.isCheckMate = true
        gameState.winner = if(playerColor == "w") "White" else "Black"
        return true
    }

    fun showWinnerDialog(context: Context, winner: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Game Over")
        builder.setMessage("$winner wins the game!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // You can add code here to reset the game if needed
        }
        builder.setNegativeButton("Play Again") { dialog, _ ->
            dialog.dismiss()
            resetBoard()
            // You can add code here to reset the game if needed
        }
        builder.setCancelable(false) // Prevents dialog from being closed without pressing OK
        val dialog = builder.create()
        dialog.show()
    }

    fun resetBoard() {
        gameState = GameState()
        setupBoard(gridLayout)
        selectedPiece = null
//        for (row in 0 until 8) {
//            var rowString = ""
//            for (col in 0 until 8) {
//                val piece = gameState.board[row][col]
//                rowString += if (piece.isNotEmpty()) "$piece " else "- "  // Use "-" for empty squares
//            }
//            Log.d(TAG, "resetBoard: Row $row: $rowString")
//        }

        chessRules = ChessRules(gameState)
        generateMoves = GenerateMoves(gameState)

//        chessRules.reset()
//        generateMoves.reset()
    }

    fun performCastling(kingRow: Int, kingCol: Int, destinationCol: Int) {
        val rookCol = if (destinationCol > kingCol) 7 else 0 // 7 for kingside, 0 for queenside
        val newRookCol = if (destinationCol > kingCol) destinationCol - 1 else destinationCol + 1

        // Move the king
        gameState.board[kingRow][destinationCol] = gameState.board[kingRow][kingCol]
        gameState.board[kingRow][kingCol] = ""

        // Move the rook to its new position
        gameState.board[kingRow][newRookCol] = gameState.board[kingRow][rookCol]
        gameState.board[kingRow][rookCol] = ""
    }

    fun updateGridLayout(gridLayout: GridLayout, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        // Get the ImageViews for the source and destination squares
        val sourceSquare = gridLayout.getChildAt(fromRow * 8 + fromCol) as ImageView
        val destinationSquare = gridLayout.getChildAt(toRow * 8 + toCol) as ImageView

        // Clear the source square's image (empty the square)
        sourceSquare.setImageDrawable(null)

        // Set the image for the destination square
        destinationSquare.setImageResource(getPieceDrawable(gameState.board[toRow][toCol]))
    }

    companion object {
        private const val LIGHT_COLOR = 0xFFF0D9B5.toInt()
        private const val DARK_COLOR = 0xFFB58863.toInt()
    }
}
