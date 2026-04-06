package com.frontend

import android.app.Activity
import android.app.AlertDialog
import com.backend.sudoku.Sudoku
import com.backend.sudoku.SudokuUtil

class SudokuSaver(val activity: Activity) {
    fun saveSudoku(fileName: String, sudoku: Sudoku, onResult: (Boolean) -> Unit) {
        if (fileName.isEmpty()) {
            showDialog("Choose a name", "", "Ok")
            onResult(false)
            return
        }

        if (!sudoku.isValid) {
            showDialog(
                "Loser Alert!",
                "You loser! Not even capable of entering a valid sudoku... Pitiful!",
                "I know... \uD83E\uDEE0"
            )
            onResult(false)
            return
        }

        if (SudokuUtil.existsOnDisk(activity.applicationContext, fileName)) {
            askUserToOverwriteFile(fileName) { overwrite ->
                if (overwrite) {
                    val success = saveSudokuToDisk(fileName, sudoku)
                    onResult(success)
                } else {
                    onResult(false)
                }
            }
        } else {
            val success = saveSudokuToDisk(fileName, sudoku)
            onResult(success)
        }
    }

    private fun saveSudokuToDisk(fileName: String, sudoku: Sudoku) : Boolean {
        val success = SudokuUtil.saveToDisk(activity.applicationContext, fileName, sudoku.asBoard, sudoku.originalList)
        if (!success) {
            showDialog(
                "Loser Alert!",
                "You loser! Not even capable of entering a valid file name... Pitiful!",
                "I know... \uD83E\uDEE0"
            )
            return false
        }

        ToastMaker.showSaveConfirmationMessage(activity.applicationContext, "$fileName saved")
        return true
    }

    private fun showDialog(title: String, message: String, buttonMessage: String) {
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonMessage) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun askUserToOverwriteFile(fileName: String, onResult: (Boolean) -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle("The file $fileName already exists. Overwrite?")
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
}