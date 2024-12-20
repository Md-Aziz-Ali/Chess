package com.example.chess

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs

class ChessBoard(private val context: Context, var gameState: GameState, var time: Int) {

    var whiteKingHasMoved = false
    var blackKingHasMoved = false
    var whiteLeftRookHasMoved = false
    var whiteRightRookHasMoved = false
    var blackLeftRookHasMoved = false
    var blackRightRookHasMoved = false

    val undoRedo = UndoRedo(context, gameState)
    private val undoImage: ImageView = (context as Activity).findViewById(R.id.imageView)
    private val redoImage: ImageView = (context as Activity).findViewById(R.id.imageView2)

    val highlight = mutableListOf<Int>()
    var generateMoves = GenerateMoves(gameState)
    val circleViews = mutableListOf<ImageView>() // List to store circle ImageViews
    var moves = mutableListOf<Pair<Boolean,Pair<Int, Int>>>()

    lateinit var gridLayout: GridLayout

    private var blackTimer: CountDownTimer? = null
    private var whiteTimer: CountDownTimer? = null

//    // Declare previousMoves as a mutable list of Move objects


    // Array to keep track of highlighted squares
//    private val highlightedSquares = mutableListOf<ImageView>()

    var selectedPiece: Pair<Int, Int>? = null  // Track the selected piece's position

    var chessRules = ChessRules(gameState)

    var currSourceSquare: ImageView? = null

    val HIGHLIGHT_SOURCE = Color.parseColor("#F6F669")  // Gold
    val HIGHLIGHT_DESTINATION = Color.parseColor("#F6F669")  // Green

    private var whiteTimerText: TextView? = null
    private var blackTimerText: TextView? = null

    init {
        // Cast context to Activity to access findViewById
        whiteTimerText = (context as Activity).findViewById(R.id.whiteTimerText)
        blackTimerText = (context as Activity).findViewById(R.id.blackTimerText)
    }

    fun updateUndoRedoButtons(canUndo: Boolean, canRedo: Boolean) {
        undoImage.let {
            it.isEnabled = canUndo
            it.alpha = if (canUndo) 1.0f else 0.5f
        }

        redoImage.let {
            it.isEnabled = canRedo
            it.alpha = if (canRedo) 1.0f else 0.5f
        }
    }

    fun startTheGame(gridLayout: GridLayout) {
        this.gridLayout = gridLayout
        gameState.whiteTimeRemaining = gameState.whiteTimeRemaining * time
        gameState.blackTimeRemaining = gameState.blackTimeRemaining * time

        if(time == 0) {
            gameState.noTimeLimit = true
            // To hide the whiteTimerText
            whiteTimerText?.visibility = View.GONE

// To hide the blackTimerText
            blackTimerText?.visibility = View.GONE
        }

        setupBoard(gridLayout)
        if(gameState.noTimeLimit == false) {
            updateTimerUI(whiteTimerText, gameState.whiteTimeRemaining / 1000)
            updateTimerUI(blackTimerText, gameState.blackTimeRemaining / 1000)
        }
    }

    fun setupBoard(gridLayout: GridLayout) {
//        this.gridLayout = gridLayout
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
                        square.setImageResource(gameState.getPieceDrawable(piece))
                    }

                    // Handle click event for each square
                    square.setOnClickListener {
                        onSquareClicked(row, col, square, gridLayout)
                    }
                }
            }
        }
        if(gameState.noTimeLimit == false)
            startWhiteTimer()
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


            removePossibleMoves(gridLayout)
        }
        undoImage.setOnClickListener {
            removePossibleMoves(gridLayout)
            undoRedo.undo(gridLayout)
            setUptTheBoardAgain()
            highlightMoves()
        }
        redoImage.setOnClickListener {
            removePossibleMoves(gridLayout)
            undoRedo.redo(gridLayout)
            setUptTheBoardAgain()
            highlightMoves()
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

    fun highlightMoves() {
        if(gameState.zeroMoves)
            return
        var fromRow = gameState.previousMoves.startPosition.first
        var fromCol = gameState.previousMoves.startPosition.second

        var toRow = gameState.previousMoves.endPosition.first
        var toCol = gameState.previousMoves.endPosition.second

        val sourceSquare = gridLayout.getChildAt(fromRow * 8 + fromCol) as ImageView
        val destinationSquare = gridLayout.getChildAt(toRow * 8 + toCol) as ImageView

        // Highlight the source square
        sourceSquare.setBackgroundColor(HIGHLIGHT_SOURCE)  // HIGHLIGHT_SOURCE is a color for highlighting the source
        // Highlight the destination square
        destinationSquare.setBackgroundColor(HIGHLIGHT_DESTINATION)  // HIGHLIGHT_DESTINATION is a color for destination

    }

    fun makeMove(gridLayout: GridLayout, row: Int, col: Int, selectedRow: Int, selectedCol: Int) {

        var previousMovesCopy = Move(
            count = 0,
            piece = "",
            startPosition = Pair(-1, -1),
            endPosition = Pair(-1, -1),
            isCastle = false,
            tookOtherPiece = "",
            tookPosition = Pair(-1, -1),
            rookStartPosition = Pair(-1, -1),
            rookEndPosition = Pair(-1, -1),
            isEnPassant = false,
            pawnPosition = Pair(-1, -1),
            isUpgraded = "",
            whiteLeftRookMoved = false,
            whiteRightRookMoved = false,
            whiteKingMoved = false,
            blackLeftRookMoved = false,
            blackRightRookMoved = false,
            blackKingMoved = false,
        )
        if(gameState.zeroMoves == false)
            previousMovesCopy = gameState.previousMoves
        // Clear previous moves before adding the new one
//        gameState.previousMoves.clear()
//        removeHighlightedMoves(gridLayout)

        // clear the redo stack
        gameState.redoStack.clear()

        // Check if the move involves a king or a rook
        val movedPiece = gameState.board[selectedRow][selectedCol]

        whiteKingHasMoved = false
        blackKingHasMoved = false
        whiteLeftRookHasMoved = false
        whiteRightRookHasMoved = false
        blackLeftRookHasMoved = false
        blackRightRookHasMoved = false


        // Check if the moved piece is a king (ends with "K")
        if (movedPiece.endsWith("K")) {
            if (movedPiece.startsWith("w") && gameState.whiteKingHasMoved == false) {  // White King
                whiteKingHasMoved = true
            } else if (movedPiece.startsWith("b") && gameState.blackKingHasMoved == false) {  // Black King
                blackKingHasMoved = true
            }
        }

        // Check if the moved piece is a rook (ends with "R")
        if (movedPiece.endsWith("R")) {
            if (movedPiece.startsWith("w")) {  // White Rook
                if (selectedCol == 0 && gameState.whiteLeftRookHasMoved == false) {  // Left Rook
                    whiteLeftRookHasMoved = true
                } else if (selectedCol == 7 && gameState.whiteRightRookHasMoved == false) {  // Right Rook
                    whiteRightRookHasMoved = true
                }
            } else if (movedPiece.startsWith("b")) {  // Black Rook
                if (selectedCol == 0 && gameState.blackLeftRookHasMoved == false) {  // Left Rook
                    blackLeftRookHasMoved = true
                } else if (selectedCol == 7 && gameState.blackRightRookHasMoved == false) {  // Right Rook
                    blackRightRookHasMoved = true
                }
            }
        }

        if(whiteKingHasMoved)
            gameState.whiteKingHasMoved = true
        if(whiteLeftRookHasMoved)
            gameState.whiteLeftRookHasMoved = true
        if(whiteRightRookHasMoved)
            gameState.whiteRightRookHasMoved = true
        if(blackKingHasMoved)
            gameState.blackKingHasMoved = true
        if(blackLeftRookHasMoved)
            gameState.blackLeftRookHasMoved = true
        if(blackRightRookHasMoved)
            gameState.blackRightRookHasMoved = true

//        if(movedPiece == "bR") {
//            Toast.makeText(context, "${gameState.blackLeftRookHasMoved} ${gameState.blackRightRookHasMoved}", Toast.LENGTH_SHORT).show()
//        }
//        if(movedPiece == "wR") {
//            Toast.makeText(context, "${gameState.whiteLeftRookHasMoved} ${gameState.whiteRightRookHasMoved}", Toast.LENGTH_SHORT).show()
//        }



        // Check if the move is a castling attempt
        if (gameState.board[selectedRow][selectedCol].endsWith("K") && abs(col - selectedCol) == 2) {
            // Call the castling function if the move meets castling conditions
            performCastling(selectedRow, selectedCol, col)

            // Add the castling move to the array
            val kingRow = selectedRow
            val kingCol = selectedCol
            val destinationCol = col
            val rookCol = if (destinationCol > kingCol) 7 else 0 // 7 for king side, 0 for queen side
            val newRookCol = if (destinationCol > kingCol) destinationCol - 1 else destinationCol + 1
            gameState.previousMoves =
                Move(
                    count = if(gameState.zeroMoves) 0 else gameState.previousMoves.count + 1,
                    piece = gameState.board[row][col],
                    startPosition = Pair(selectedRow, selectedCol),
                    endPosition = Pair(selectedRow, col),
                    isCastle = true, // Mark this as a castling move
                    tookOtherPiece = "",
                    tookPosition = Pair(-1, -1),
                    rookStartPosition = Pair(kingRow, rookCol),
                    rookEndPosition = Pair(kingRow, newRookCol),
                    isEnPassant = false,
                    pawnPosition = Pair(-1, -1),
                    isUpgraded = "",
                    whiteLeftRookMoved = whiteLeftRookHasMoved,
                    whiteRightRookMoved = whiteRightRookHasMoved,
                    whiteKingMoved = whiteKingHasMoved,
                    blackLeftRookMoved = blackLeftRookHasMoved,
                    blackRightRookMoved = blackRightRookHasMoved,
                    blackKingMoved = blackKingHasMoved,
                )

            gameState.undoStack.add(gameState.previousMoves)
//            ToastPrint()

//            updateGridLayout(gridLayout, selectedRow, rookCol, selectedRow, newRookCol)
            setUptTheBoardAgain()
//            highlightMoves(gridLayout, selectedRow, selectedCol,selectedRow, rookCol)
            highlightMoves()
            // Switch turns after a valid move
//            gameState.switchTurn()
            checkCheckMateAndSwitchTurn()
            // Clear the selection
            selectedPiece = null
            gameState.zeroMoves = false

        }
        // check for en passant
        else if(movedPiece.endsWith("P") && gameState.board[row][col] == "" && col != selectedCol && gameState.zeroMoves == false) {
            val removeRowPiece = previousMovesCopy.endPosition.first
            val removeColPiece = previousMovesCopy.endPosition.second

            val removePieceSquare = gridLayout.getChildAt(removeRowPiece * 8 + removeColPiece) as ImageView
            removePieceSquare.setImageDrawable(null)

            // move
            gameState.board[row][col] = gameState.board[selectedRow][selectedCol]
            gameState.board[selectedRow][selectedCol] = ""


            // add the move to the array
            gameState.previousMoves =
                Move(
                    count = if(gameState.zeroMoves) 0 else gameState.previousMoves.count + 1,
                    piece = gameState.board[row][col],
                    startPosition = Pair(selectedRow, selectedCol),
                    endPosition = Pair(row, col),
                    isCastle = false,  // Mark this as a regular move
                    tookOtherPiece = if (gameState.isWhiteTurn) "bP" else "wP",
                    tookPosition = Pair(removeRowPiece, removeColPiece),
                    rookStartPosition = Pair(-1, -1),
                    rookEndPosition = Pair(-1, -1),
                    isEnPassant = true,
                    pawnPosition = Pair(removeRowPiece, removeColPiece),
                    isUpgraded = "",
                    whiteLeftRookMoved = whiteLeftRookHasMoved,
                    whiteRightRookMoved = whiteRightRookHasMoved,
                    whiteKingMoved = whiteKingHasMoved,
                    blackLeftRookMoved = blackLeftRookHasMoved,
                    blackRightRookMoved = blackRightRookHasMoved,
                    blackKingMoved = blackKingHasMoved,
                )
            gameState.board[removeRowPiece][removeColPiece] = ""


            gameState.undoStack.add(gameState.previousMoves)
//            ToastPrint()

//            updateGridLayout(gridLayout, selectedRow, selectedCol, row, col)
            setUptTheBoardAgain()
            highlightMoves()
            // Switch turns after a valid move
//            gameState.switchTurn()
            checkCheckMateAndSwitchTurn()
            // Clear the selection
            selectedPiece = null
            gameState.zeroMoves = false
        }
        else {
            // Regular move
            if(gameState.board[selectedRow][selectedCol].endsWith("P") && (row == 0 || row == 7)) {
                showImagePopup(selectedRow, selectedCol, row, col)
//                updateGridLayout(gridLayout, selectedRow, selectedCol, row, col)
//                resetBoard()
            }
            else {
                // Add the regular move to the array
                gameState.previousMoves =
                    Move(
                        count = if(gameState.zeroMoves) 0 else gameState.previousMoves.count + 1,
                        piece = gameState.board[selectedRow][selectedCol],
                        startPosition = Pair(selectedRow, selectedCol),
                        endPosition = Pair(row, col),
                        isCastle = false,
                        tookOtherPiece = gameState.board[row][col],
                        tookPosition = Pair(selectedRow, selectedCol),
                        rookStartPosition = Pair(-1, -1),
                        rookEndPosition = Pair(-1, -1),
                        isEnPassant = false,
                        pawnPosition = Pair(-1, -1),  // Mark this as a regular move
                        isUpgraded = "",
                        whiteLeftRookMoved = whiteLeftRookHasMoved,
                        whiteRightRookMoved = whiteRightRookHasMoved,
                        whiteKingMoved = whiteKingHasMoved,
                        blackLeftRookMoved = blackLeftRookHasMoved,
                        blackRightRookMoved = blackRightRookHasMoved,
                        blackKingMoved = blackKingHasMoved,
                    )


                gameState.undoStack.add(gameState.previousMoves)
                gameState.zeroMoves = false

//                ToastPrint()

                gameState.board[row][col] = gameState.board[selectedRow][selectedCol]
                gameState.board[selectedRow][selectedCol] = ""
//                updateGridLayout(gridLayout, selectedRow, selectedCol, row, col)
                setUptTheBoardAgain()
                highlightMoves()
                checkCheckMateAndSwitchTurn()
            }

//            updateGridLayout(gridLayout, selectedRow, selectedCol, row, col)
//            highlightMoves(gridLayout, selectedRow, selectedCol, row, col)
        }
    }

    // Function to check checkmate
    fun isCheckMate(playerColor: String): Boolean {
        Log.d(TAG, "${gameState.isWhiteTurn}")
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
        gameState.isDraw = true
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

    fun print() {

        for (row in 0 until 8) {
            var rowString = ""
            for (col in 0 until 8) {
                val piece = gameState.board[row][col]
                rowString += if (piece.isNotEmpty()) "$piece " else "-- "  // Use "-" for empty squares
            }
            Log.d(TAG, "resetBoard: Row $row: $rowString")
        }
    }

    fun setUptTheBoardAgain() {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val piece = gameState.board[row][col]
                val square = gridLayout.getChildAt(row * 8 + col) as ImageView

                // Set alternating background color for the chessboard squares
                val backgroundColor = if ((row + col) % 2 == 0) LIGHT_COLOR else DARK_COLOR
                square.setBackgroundColor(backgroundColor)

                // Set the piece image if present, otherwise clear the square
                if (piece.isNotEmpty()) {
//                    val drawable = gameState.getPieceDrawable(piece) // This method retrieves drawable for piece type
                    square.setImageResource(gameState.getPieceDrawable(piece))
                } else {
                    square.setImageDrawable(null) // Empty square if no piece
                }
            }
        }
    }

    fun resetBoard() {
        gameState = GameState()
        highlight.clear()
        circleViews.clear()
        moves.clear()
        selectedPiece = null
        currSourceSquare = null

        chessRules = ChessRules(gameState)
        generateMoves = GenerateMoves(gameState)

        setUptTheBoardAgain()
    }



    fun performCastling(kingRow: Int, kingCol: Int, destinationCol: Int) {
        val rookCol = if (destinationCol > kingCol) 7 else 0 // 7 for king side, 0 for queen side
        val newRookCol = if (destinationCol > kingCol) destinationCol - 1 else destinationCol + 1

        // Move the king
        gameState.board[kingRow][destinationCol] = gameState.board[kingRow][kingCol]
        gameState.board[kingRow][kingCol] = ""

        // Move the rook to its new position
        gameState.board[kingRow][newRookCol] = gameState.board[kingRow][rookCol]
        gameState.board[kingRow][rookCol] = ""
    }

    fun saveTheMoveForUpgradation(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, upgradedTo: String) {
        gameState.previousMoves = Move(
            count = if(gameState.zeroMoves) 0 else gameState.previousMoves.count + 1,
            piece = gameState.board[fromRow][fromCol],
            startPosition = Pair(fromRow, fromCol),
            endPosition = Pair(toRow, toCol),
            isCastle = false,
            rookStartPosition = Pair(-1, -1),
            rookEndPosition = Pair(-1, -1),
            isEnPassant = false,
            pawnPosition = Pair(-1, -1),
            tookOtherPiece = gameState.board[toRow][toCol],
            tookPosition = Pair(-1, -1),
            isUpgraded = upgradedTo,
            whiteLeftRookMoved = whiteLeftRookHasMoved,
            whiteRightRookMoved = whiteRightRookHasMoved,
            whiteKingMoved = whiteKingHasMoved,
            blackLeftRookMoved = blackLeftRookHasMoved,
            blackRightRookMoved = blackRightRookHasMoved,
            blackKingMoved = blackKingHasMoved,
        )
        gameState.undoStack.add(gameState.previousMoves)
//        val a = gameState.previousMoves.tookOtherPiece
//        Toast.makeText(context, "${a}", Toast.LENGTH_SHORT).show()
    }

    fun showImagePopup(fromRow: Int, fromCol: Int, row: Int, col: Int) {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_dialog, null)

        // Find the ImageViews in the layout if you want to set them programmatically
        val image1 = dialogView.findViewById<ImageView>(R.id.image1)
        val image2 = dialogView.findViewById<ImageView>(R.id.image2)
        val image3 = dialogView.findViewById<ImageView>(R.id.image3)
        val image4 = dialogView.findViewById<ImageView>(R.id.image4)

        // Set images programmatically
        if(gameState.isWhiteTurn) {
            image1.setImageResource(R.drawable.rook_white)
            image2.setImageResource(R.drawable.bishop_white)
            image3.setImageResource(R.drawable.knight_white)
            image4.setImageResource(R.drawable.queen_white)
        }

        // Build the AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false) // Prevent closing by tapping outside
            .create()

        // Set click listeners to dismiss the dialog when an image is selected
        image1.setOnClickListener {
            // Perform action for Image 1
            var upgradedTo = if(gameState.isWhiteTurn) "wR" else "bR"
            saveTheMoveForUpgradation(fromRow, fromCol, row, col, upgradedTo)
            gameState.board[row][col] = if(gameState.isWhiteTurn) "wR" else "bR"
            gameState.board[fromRow][fromCol] = ""
//            updateGridLayout(gridLayout, fromRow, fromCol, row, col)
            setUptTheBoardAgain()
            highlightMoves()
            checkCheckMateAndSwitchTurn()
            dialog.dismiss() // Close the dialog
        }
        image2.setOnClickListener {
            // Perform action for Image 2
            var upgradedTo = if(gameState.isWhiteTurn) "wB" else "bB"
            saveTheMoveForUpgradation(fromRow, fromCol, row, col, upgradedTo)
            gameState.board[row][col] = if(gameState.isWhiteTurn) "wB" else "bB"
            gameState.board[fromRow][fromCol] = ""
//            updateGridLayout(gridLayout, fromRow, fromCol, row, col)
            setUptTheBoardAgain()
            highlightMoves()
            checkCheckMateAndSwitchTurn()
            dialog.dismiss() // Close the dialog
        }
        image3.setOnClickListener {
            // Perform action for Image 3
            var upgradedTo = if(gameState.isWhiteTurn) "wN" else "bN"
            saveTheMoveForUpgradation(fromRow, fromCol, row, col, upgradedTo)
            gameState.board[row][col] = if(gameState.isWhiteTurn) "wN" else "bN"
            gameState.board[fromRow][fromCol] = ""
//            updateGridLayout(gridLayout, fromRow, fromCol, row, col)
            setUptTheBoardAgain()
            highlightMoves()
            checkCheckMateAndSwitchTurn()
            dialog.dismiss() // Close the dialog
        }
        image4.setOnClickListener {
            // Perform action for Image 3
            var upgradedTo = if(gameState.isWhiteTurn) "wQ" else "bQ"
            saveTheMoveForUpgradation(fromRow, fromCol, row, col, upgradedTo)
            gameState.board[row][col] = if(gameState.isWhiteTurn) "wQ" else "bQ"
            gameState.board[fromRow][fromCol] = ""
//            updateGridLayout(gridLayout, fromRow, fromCol, row, col)
            setUptTheBoardAgain()
            highlightMoves()
            checkCheckMateAndSwitchTurn()
            dialog.dismiss() // Close the dialog
        }
//        updateGridLayout(gridLayout, fromRow, fromCol, row, col)

        // Show the dialog
//        resetBoard()
        dialog.show()
    }

    fun checkStaleMate(playerColor: String): Boolean {
        val opponentColor = if (playerColor == "w") "b" else "w"
        var boardCopy = chessRules.copyBoard(gameState.board)

        if(chessRules.isOnlyKingPresent()) {
            gameState.isDraw = true
            return true
        }

        if(chessRules.isKingInCheck(opponentColor, boardCopy))
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
        gameState.isDraw = true
//        gameState.winner = if(playerColor == "w") "White" else "Black"
        return true
    }

    fun checkCheckMateAndSwitchTurn() {
        val currentPlayer = if(gameState.isWhiteTurn) "w" else "b"
        if(isCheckMate(currentPlayer)) {
            blackTimer?.cancel()
            whiteTimer?.cancel()
            showWinnerDialog(context, gameState.winner)
        }
        else if(checkStaleMate(currentPlayer)){
            showDrawDialog(context)
//            Toast.makeText(context, "not checkmate", Toast.LENGTH_SHORT).show()
        }

        if(gameState.noTimeLimit == false) {
            if (gameState.isWhiteTurn == false) {
                startWhiteTimer()
                blackTimer?.cancel()
            } else {
                startBlackTimer()
                whiteTimer?.cancel()
            }
        }

        // Switch turns after a valid move
        gameState.switchTurn()

        // printing the board
//        print()
        gameState.zeroMoves = false

        // Clear the selection
        selectedPiece = null
    }

    fun showDrawDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Game Over")
        builder.setMessage("Draw the game!")
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

    private fun startWhiteTimer() {
        whiteTimer?.cancel() // Cancel any existing timer
        whiteTimer = object : CountDownTimer(gameState.whiteTimeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                gameState.whiteTimeRemaining = millisUntilFinished
                updateTimerUI(whiteTimerText, millisUntilFinished)
            }

            override fun onFinish() {
                // White's time is up
                removePossibleMoves(gridLayout)
                gameState.winner = "Black"
                showWinnerDialog(context, gameState.winner)
            }
        }.start()
    }

    private fun startBlackTimer() {
        blackTimer?.cancel() // Cancel any existing timer
        blackTimer = object : CountDownTimer(gameState.blackTimeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                gameState.blackTimeRemaining = millisUntilFinished
                updateTimerUI(blackTimerText, millisUntilFinished)
            }

            override fun onFinish() {
                // Black's time is up
//                showGameOver("White wins! Black ran out of time.")
                removePossibleMoves(gridLayout)
                gameState.winner = "White"
                showWinnerDialog(context, gameState.winner)
            }
        }.start()
    }

    private fun updateTimerUI(timerText: TextView?, millisUntilFinished: Long) {
        val seconds = millisUntilFinished / 1000
        timerText?.text = String.format("%d:%02d", seconds / 60, seconds % 60)
    }

    companion object {
        private const val LIGHT_COLOR = 0xFFEEEED2.toInt()
        private const val DARK_COLOR = 0xFF769656.toInt()
    }
}
