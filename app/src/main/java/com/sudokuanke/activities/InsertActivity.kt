package com.sudokuanke.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.backend.sudoku.Sudoku
import com.backend.sudoku.SudokuImpl
import com.backend.sudoku.SudokuUtil
import com.frontend.NumberSelector
import com.frontend.SudokuGridView
import com.frontend.SudokuSaver
import com.frontend.Undoer
import com.sudokuanke.R

class InsertActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.insert_page)

        val boardAsString: String? = intent.extras?.getString("board")

        val grid = findViewById<SudokuGridView>(R.id.sudokuGridView)
        val sudoku: Sudoku = SudokuImpl()

        if (boardAsString != null) {
            sudoku.init(SudokuUtil.fromString(boardAsString), null)
        }

        val undoer = Undoer()
        grid.setUndoer(undoer)

        grid.setSudoku(sudoku)

        val selector = findViewById<NumberSelector>(R.id.numberSelector)

        selector.onDigitSelected = { digit ->
            grid.setSelectedDigit(digit)
        }

        val exitButton = findViewById<Button>(R.id.exitButton)
        exitButton.setOnClickListener {
            exit()
        }

        val undoButton = findViewById<Button>(R.id.undoButton)
        undoButton.setOnClickListener {
            val previousPlay = undoer.previousPlay
            if (previousPlay != null) {
                sudoku.place(previousPlay.row, previousPlay.column, previousPlay.previousDigit)
                grid.refreshValues()
            }
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        val fileName = findViewById<EditText>(R.id.sudokuName)
        saveButton.setOnClickListener {
            val fileNameString: String = fileName.text.toString()
            val sudokuSaver = SudokuSaver(this)
            sudokuSaver.saveSudoku(fileNameString, sudoku) { success ->
                if (success) {
                    exit()
                }
            }
        }
    }

    private fun exit() {
        finish()
    }
}