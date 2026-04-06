package com.sudokuanke.activities

import android.app.AlertDialog
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.backend.sudoku.Sudoku
import com.backend.sudoku.SudokuGenerator
import com.backend.sudoku.SudokuImpl
import com.backend.sudoku.SudokuSolver
import com.backend.sudoku.SudokuUtil
import com.frontend.NumberSelector
import com.frontend.SudokuGridView
import com.frontend.SudokuSaver
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
            sudoku.init(generator.generate(SudokuGenerator.Difficulty.EVIL), null)
        } else {
            val board = boardAsString.substring(0, 81)
            val originalValues = boardAsString.substring(82, 163)
            val originalList = BooleanArray(81)
            for (index in 0..80) {
                originalList[index] = originalValues[index] == '1'
            }

            sudoku.init(SudokuUtil.fromString(board), originalList)
        }

        grid.setSudoku(sudoku)

        val selector = findViewById<NumberSelector>(R.id.numberSelector)

        selector.onDigitSelected = { digit ->
            grid.setSelectedDigit(digit)
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
        val fileName = findViewById<EditText>(R.id.sudokuName)
        saveButton.setOnClickListener {
            val fileNameString: String = fileName.text.toString()
            val sudokuSaver = SudokuSaver(this)
            sudokuSaver.saveSudoku(fileNameString, sudoku) {}
        }

        grid.checkValidity = {
            if (sudoku.isComplete) {
                if (sudoku.isValid) {
                    showWinDialog()
                } else {
                    showLoseDialog()
                }
            }
        }
    }

    private fun startNewGame() {
        sudoku.init(generator.generate(SudokuGenerator.Difficulty.EVIL), null)
        grid.setSudoku(sudoku)
        grid.setUndoer(Undoer())
    }

    private fun showWinDialog() {
        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.fireworks_animation)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.fireworks_dialog_height)
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("🎉 You won!")
            .setMessage("Congratulations, you solved the puzzle!")
            .setView(imageView)
            .setPositiveButton("New game") { _, _ -> startNewGame() }
            .setNegativeButton("Keep playing") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            (imageView.drawable as AnimationDrawable).start()
        }
        dialog.setOnDismissListener {
            (imageView.drawable as AnimationDrawable).stop()
        }
        dialog.show()
    }

    private fun showLoseDialog() {
        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.kut)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.fireworks_dialog_height)
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        AlertDialog.Builder(this)
            .setTitle("❌ Not quite...")
            .setMessage("The board is complete but contains errors.")
            .setView(imageView)
            .setPositiveButton("New game") { _, _ -> startNewGame() }
            .setNegativeButton("Keep playing") { d, _ -> d.dismiss() }
            .show()
    }
}