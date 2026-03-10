package com.sudokuanke.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
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

        val takeImageButton = findViewById<Button>(R.id.take_image_button)
        takeImageButton?.setOnClickListener {
            startCameraCapture()
        }
        val readImageButton = findViewById<Button>(R.id.read_image_button)
        readImageButton?.setOnClickListener {
            readSudoku()
        }
    }

    private var photoUriSet: Boolean = false
    private lateinit var photoUri: Uri
    private lateinit var imageView: ImageView
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageView.setImageURI(photoUri)
            } else {
                Log.e("Error", "Take picture unsuccessful")
            }
        }

    private fun startCameraCapture() {
        imageView = findViewById<ImageView>(R.id.image_view)
        imageView.visibility = View.VISIBLE
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

    private fun readSudoku() {
        if (!photoUriSet) {
            Log.i("Sudoku", "photoUri not initialized")
            return
        }

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE
        imageView.visibility = View.GONE

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val image = InputImage.fromFilePath(applicationContext, photoUri)
                sudokuReader.setImage(image)
            }

            sudokuReader.readSudoku() { sudoku ->
                progressBar.visibility = View.GONE
                startInsertActivity(SudokuUtil.toString(sudoku))
            }
        }
    }

    private fun startInsertActivity(boardAsString : String) {
        val intent = Intent(this, InsertActivity::class.java)
        intent.putExtra("board", boardAsString)
        finish()
        startActivity(intent)
    }
}
