package com.example.chess

import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChessActivity : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private lateinit var chessGame: ChessGame
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chess)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the GridLayout in your layout
        gridLayout = findViewById(R.id.chessBoard)
        var defaultValue = 0
        val time = intent.getIntExtra("time", defaultValue)
        var isOnline = intent.getBooleanExtra("isOnline", false)
        var name = intent.getStringExtra("name")
        var receiverId = intent.getStringExtra("receiverId")



        // Create the ChessGame and start the game
        chessGame = ChessGame(this, time, isOnline, name.toString(), receiverId.toString())
        chessGame.startGame(gridLayout)
    }
}