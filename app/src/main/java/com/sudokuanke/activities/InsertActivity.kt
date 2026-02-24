package com.sudokuanke.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.backend.Sudoku
import com.backend.SudokuImpl
import com.backend.SudokuUtil
import com.frontend.NumberSelector
import com.frontend.SudokuGridView
import com.frontend.Undoer
import com.sudokuanke.R

class InsertActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.insert_page)

        val grid = findViewById<SudokuGridView>(R.id.sudokuGridView)
        val sudoku: Sudoku = SudokuImpl()

        val undoer: Undoer = Undoer()
        grid.setUndoer(undoer)

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

        val undoButton = findViewById<Button>(R.id.undoButton)
        undoButton.setOnClickListener {
            val previousPlay = undoer.previousPlay
            if (previousPlay != null) {
                sudoku.place(previousPlay.row, previousPlay.column, previousPlay.previousDigit)
                grid.refreshValues()
            }
        }

        val fileName = findViewById<EditText>(R.id.sudokuName)
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            if (fileName.text.toString().isEmpty()) {
                showDialog("Choose a name", "", "Ok")
                return@setOnClickListener
            }

            if (!sudoku.isValid) {
                showDialog(
                    "Loser Alert!",
                    "You loser! Not even capable of entering a valid sudoku... Pitiful!",
                    "I know... \uD83E\uDEE0"
                )
                return@setOnClickListener
            }

            val success = SudokuUtil.saveToDisk(applicationContext, fileName.text.toString(), sudoku.asBoard)
            if (!success) {
                showDialog(
                    "Loser Alert!",
                    "You loser! Not even capable of entering a valid file name... Pitiful!",
                    "I know... \uD83E\uDEE0"
                )
                return@setOnClickListener
            }

            exit()
        }
    }

    private fun showDialog(title: String, message: String, buttonMessage: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonMessage) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun exit() {
        finish()
    }
}