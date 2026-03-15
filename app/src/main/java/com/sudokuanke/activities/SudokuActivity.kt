package com.sudokuanke.activities

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.backend.sudoku.Sudoku
import com.backend.sudoku.SudokuGenerator
import com.backend.sudoku.SudokuImpl
import com.backend.sudoku.SudokuSolver
import com.backend.sudoku.SudokuUtil
import com.frontend.NumberSelector
import com.frontend.SudokuGridView
import com.frontend.Undoer
import com.sudokuanke.R

class SudokuActivity : ComponentActivity() {
    var sudoku : Sudoku = SudokuImpl()
    lateinit var grid : SudokuGridView
    var generator : SudokuGenerator = SudokuGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        val boardAsString : String? = intent.extras?.getString("board")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sudoku_page)

        grid= findViewById(R.id.sudokuGridView)

        val undoer = Undoer()
        grid.setUndoer(undoer)

        if (boardAsString == null) {
            sudoku.init(generator.generate(SudokuGenerator.Difficulty.EVIL))
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
            exit()
        }

        val emptyButton = findViewById<Button>(R.id.emptyButton)
        emptyButton.setOnClickListener {
            sudoku.clearNonOriginals()
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

        val fireworksImage = findViewById<ImageView>(R.id.fireworksImage)

        grid.checkValidity = {
            if (sudoku.isComplete) {
                if (sudoku.isValid) {
                    grid.visibility = View.INVISIBLE
                    fireworksImage.visibility = View.VISIBLE
                    (fireworksImage.drawable as AnimationDrawable).start()

                    Handler(Looper.getMainLooper()).postDelayed({
                        (fireworksImage.drawable as AnimationDrawable).stop()
                        fireworksImage.visibility = View.GONE
                        grid.visibility = View.VISIBLE
                        showDialog("🎉 You won!", "Congratulations, you solved the puzzle!")
                    }, 5000)
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
                sudoku.init(generator.generate(SudokuGenerator.Difficulty.EVIL))
                grid.setSudoku(sudoku)
                val undoer = Undoer()
                grid.setUndoer(undoer)
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