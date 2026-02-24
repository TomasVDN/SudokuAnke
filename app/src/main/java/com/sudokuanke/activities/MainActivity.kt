package com.sudokuanke.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.sudokuanke.R


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.start_page)

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            startSudokuActivity()
        }

        val loadButton = findViewById<Button>(R.id.loadButton)
        loadButton.setOnClickListener {
            loadActivity()
        }

        val insertBoardButton = findViewById<Button>(R.id.insertBoardButton)
        insertBoardButton.setOnClickListener {
            startInsertBoardActivity()
        }
    }

    private fun startSudokuActivity() {
        val intent = Intent(this, SudokuActivity::class.java)
        startActivity(intent)
    }

    private fun loadActivity() {
        val intent = Intent(this, LoadActivity::class.java)
        startActivity(intent)
    }

    private fun startInsertBoardActivity() {
        val intent = Intent(this, InsertActivity::class.java)
        startActivity(intent)
    }
}