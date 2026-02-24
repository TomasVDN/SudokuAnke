package com.example.sudokuanke

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sudoku_page)

        val grid = findViewById<SudokuGridView>(R.id.sudokuGridView)
        val sudoku: Sudoku = SudokuImpl()

        val undoer: Undoer = Undoer()
        grid.setUndoer(undoer)

        val generator = SudokuGenerator()

        sudoku.init(generator.generate(SudokuGenerator.Difficulty.EASY))
        grid.setSudoku(sudoku)

        val selector = findViewById<NumberSelector>(R.id.numberSelector)

        selector.onDigitSelected = { digit ->
            grid.setSelectedDigit(digit)
            SudokuUtil.printToSystemOut(sudoku.asBoard)
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
        val loadButton = findViewById<Button>(R.id.loadButton)

        saveButton.setOnClickListener {
            SudokuUtil.saveToDisk(applicationContext,findViewById<EditText>(R.id.sudokuName).text.toString(), sudoku.asBoard)
        }

        loadButton.setOnClickListener {
            // TODO[Tomas] handle exception... For now, the app crashes :D
            val board = SudokuUtil.getFromDisk(applicationContext, findViewById<EditText>(R.id.sudokuName).text.toString())
            sudoku.init(board)
            grid.setSudoku(sudoku)
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
}
