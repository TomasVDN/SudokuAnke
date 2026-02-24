package com.sudokuanke.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.backend.Sudoku
import com.backend.SudokuGenerator
import com.backend.SudokuImpl
import com.backend.SudokuSolver
import com.backend.SudokuUtil
import com.frontend.NumberSelector
import com.frontend.SudokuGridView
import com.frontend.Undoer
import com.sudokuanke.R

class SudokuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val boardAsString : String? = intent.extras?.getString("board")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sudoku_page)

        val grid = findViewById<SudokuGridView>(R.id.sudokuGridView)
        val sudoku: Sudoku = SudokuImpl()

        val undoer: Undoer = Undoer()
        grid.setUndoer(undoer)

        if (boardAsString == null) {
            val generator = SudokuGenerator()
            sudoku.init(generator.generate(SudokuGenerator.Difficulty.EASY))
        } else {
            sudoku.init(SudokuUtil.fromString(boardAsString))
        }

        grid.setSudoku(sudoku)

        val selector = findViewById<NumberSelector>(R.id.numberSelector)

        selector.onDigitSelected = { digit ->
            grid.setSelectedDigit(digit)
            SudokuUtil.printToSystemOut(sudoku.asBoard)
        }

        val exitButton = findViewById<Button>(R.id.exitButton)
        exitButton.setOnClickListener {
            sudoku.clear()
            grid.refreshValues()
            exit()
        }

        val emptyButton = findViewById<Button>(R.id.emptyButton)
        emptyButton.setOnClickListener {
            sudoku.clear()
            grid.refreshValues()
        }


        val undoButton = findViewById<Button>(R.id.undoButton)
        undoButton.setOnClickListener {
            val previousPlay = undoer.previousPlay
            if (previousPlay != null) {
                sudoku.place(previousPlay.row, previousPlay.column, previousPlay.previousDigit)
                grid.refreshValues()
            }
        }

        val undoUntilValidButton = findViewById<Button>(R.id.undoUntilValidButton)
        undoUntilValidButton.setOnClickListener {
            var previousPlay = undoer.previousPlay
            while (previousPlay != null) {
                sudoku.place(previousPlay.row, previousPlay.column, previousPlay.previousDigit)
                if (previousPlay.wasValid()) {
                    break
                } else {
                    previousPlay = undoer.previousPlay
                }
            }
            grid.refreshValues()
        }

        val solveButton = findViewById<Button>(R.id.solveButton)
        solveButton.setOnClickListener {
            val sudokuSolver = SudokuSolver(sudoku)
            sudokuSolver.solve()
            grid.refreshValues()
        }

        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            SudokuUtil.saveToDisk(applicationContext,findViewById<EditText>(R.id.sudokuName).text.toString(), sudoku.asBoard)
        }

        grid.checkValidity = {
            if (sudoku.isComplete) {
                if (sudoku.isValid) {
                    showDialog("🎉 You won!", "Congratulations, you solved the puzzle!")
                } else {
                    showDialog("❌ Not quite...", "The board is complete but contains errors.")
                }
            }


        }


    }
    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("New game") { _, _ ->
                //sudoku.init(generator.generate(SudokuGenerator.Difficulty.EVIL))
                //grid.setSudoku(sudoku)
            }
            .setNegativeButton("Keep playing") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun exit() {
        finish()
    }
}