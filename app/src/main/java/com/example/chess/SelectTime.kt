package com.example.chess

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chess.databinding.ActivitySelectTimeBinding

class SelectTime : AppCompatActivity() {
    private lateinit var binding: ActivitySelectTimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySelectTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imageView7.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.bulletButton.setOnClickListener {
            val intent = Intent(this, ChessActivity::class.java)
            intent.putExtra("time", 1)
            startActivity(intent)
            finish()
        }

        binding.blitzButton.setOnClickListener {
            val intent = Intent(this, ChessActivity::class.java)
            intent.putExtra("time", 3)
            startActivity(intent)
            finish()
        }

        binding.rapidButton.setOnClickListener {
            val intent = Intent(this, ChessActivity::class.java)
            intent.putExtra("time", 10)
            startActivity(intent)
            finish()
        }

        binding.notimelimit.setOnClickListener {
            val intent = Intent(this, ChessActivity::class.java)
            intent.putExtra("time", 0)
            startActivity(intent)
            finish()
        }

        binding.imageView23.setOnClickListener {
            val intent = Intent(this, ChessActivity::class.java)
            intent.putExtra("time", 30)
            startActivity(intent)
            finish()
        }

        binding.imageView21.setOnClickListener {
            val intent = Intent(this, ChessActivity::class.java)
            intent.putExtra("time", 5)
            startActivity(intent)
            finish()
        }
    }
}