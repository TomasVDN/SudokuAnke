package com.sudokuanke.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.sudokuanke.R

class MainActivity : ComponentActivity() {

    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.start_page)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            startActivity(Intent(this, SudokuActivity::class.java))
        }
        findViewById<Button>(R.id.loadButton).setOnClickListener {
            startActivity(Intent(this, LoadActivity::class.java))
        }
        findViewById<Button>(R.id.insertBoardButton).setOnClickListener {
            startActivity(Intent(this, InsertActivity::class.java))
        }
        findViewById<Button>(R.id.importBoardButton).setOnClickListener {
            startActivity(Intent(this, ImportActivity::class.java))
        }
        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
        }
    }
}
