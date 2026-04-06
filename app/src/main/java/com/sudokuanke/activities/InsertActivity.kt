package com.sudokuanke.activities

import android.app.AlertDialog
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
import com.frontend.ToastMaker
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
        saveButton.setOnClickListener {
            saveSudoku(sudoku)
        }
    }

    private fun saveSudoku(sudoku: Sudoku) {
        val fileName = findViewById<EditText>(R.id.sudokuName)
        val fileNameString: String = fileName.text.toString()
        if (fileNameString.isEmpty()) {
            showDialog("Choose a name", "", "Ok")
            return
        }

        if (!sudoku.isValid) {
            showDialog(
                "Loser Alert!",
                "You loser! Not even capable of entering a valid sudoku... Pitiful!",
                "I know... \uD83E\uDEE0"
            )
            return
        }

        if (SudokuUtil.existsOnDisk(applicationContext, fileNameString)) {
            askUserToOverwriteFile() { overwrite ->
                if (overwrite) {
                    saveSudokuToDiskAndExit(fileNameString, sudoku)
                }
            }
        } else {
            saveSudokuToDiskAndExit(fileNameString, sudoku)
        }
    }

    private fun saveSudokuToDiskAndExit(fileName: String, sudoku: Sudoku) {
        val success = SudokuUtil.saveToDisk(applicationContext, fileName, sudoku.asBoard, sudoku.originalList)
        if (!success) {
            showDialog(
                "Loser Alert!",
                "You loser! Not even capable of entering a valid file name... Pitiful!",
                "I know... \uD83E\uDEE0"
            )
            return
        }

        ToastMaker.showSaveConfirmationMessage(applicationContext, "File $fileName saved")
        exit()
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

    private fun askUserToOverwriteFile(onResult: (Boolean) -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("The file already exists. Overwrite?")
            .setPositiveButton("Yes") { dialog, _ ->
                onResult(true)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                onResult(false)
                dialog.dismiss()
            }
            .setOnCancelListener() {
                onResult(false)
            }
            .show()
    }

    private fun exit() {
        finish()
    }
}