package com.sudokuanke.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.backend.ocr.SudokuReader
import com.backend.sudoku.SudokuUtil
import com.google.mlkit.vision.common.InputImage
import com.sudokuanke.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImportActivity : ComponentActivity() {
    private val sudokuReader = SudokuReader()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.import_page)

        val takePictureButton = findViewById<Button>(R.id.takePictureButton)
        takePictureButton.setOnClickListener {
            startCameraCapture()
        }

        val loadPictureButton = findViewById<Button>(R.id.loadPictureButton)
        loadPictureButton.setOnClickListener {
            loadPicture()
        }
    }

    private var photoUriSet: Boolean = false
    private lateinit var photoUri: Uri

    //----------------------------------------------------------------------------------------------
    // TAKE PICTURE
    //----------------------------------------------------------------------------------------------

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                readSudoku()
            } else {
                Log.e("ImportActivity", "Take picture unsuccessful")
                finish()
            }
        }

    private fun startCameraCapture() {
        lifecycleScope.launch {
            val photoFile = withContext(Dispatchers.IO) {
                createImageFile()
            }
            photoUri = FileProvider.getUriForFile(
                this@ImportActivity,
                "${packageName}.provider",
                photoFile
            )
            photoUriSet = true

            takePictureLauncher.launch(photoUri)
        }
    }

    private fun createImageFile(): File {
        return File.createTempFile(
            "IMG_",
            ".jpg",
            filesDir
        )
    }

    //----------------------------------------------------------------------------------------------
    // LOAD PICTURE
    //----------------------------------------------------------------------------------------------

    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                photoUri = uri
                photoUriSet = true
                readSudoku()
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun loadPicture() {
        lifecycleScope.launch {
            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    //----------------------------------------------------------------------------------------------
    // READ SUDOKU
    //----------------------------------------------------------------------------------------------

    private fun readSudoku() {
        if (!photoUriSet) {
            Log.i("ImportActivity", "photoUri not initialized")
            return
        }

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val image = InputImage.fromFilePath(applicationContext, photoUri)
                sudokuReader.setImage(image)
            }

            sudokuReader.readSudoku() { sudokuWithNumberOfIssues ->
                progressBar.visibility = View.GONE
                val numberOfIssues = sudokuWithNumberOfIssues.second
                val sudoku = SudokuUtil.toString(sudokuWithNumberOfIssues.first)
                if (sudokuWithNumberOfIssues.second > 0) {
                    startInsertActivityIfUserAccepts(sudoku, numberOfIssues)
                } else {
                    startInsertActivity(sudoku)
                }
            }
        }
    }

    private fun startInsertActivity(boardAsString: String) {
        val intent = Intent(this, InsertActivity::class.java)
        intent.putExtra("board", boardAsString)
        finish()
        startActivity(intent)
    }

    private fun startInsertActivityIfUserAccepts(boardAsString: String, numberOfIssues: Int) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("⚠\uFE0F Issues")
                .setMessage("We found $numberOfIssues issue${if (numberOfIssues != 1) "s" else ""} \uD83D\uDE2D")
                .setPositiveButton("Continue") { _, _ ->
                    startInsertActivity(boardAsString)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    finish()
                }
                .setOnDismissListener {
                    finish()
                }
                .show()
        }
    }
}
