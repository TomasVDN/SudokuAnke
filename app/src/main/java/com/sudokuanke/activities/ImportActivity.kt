package com.sudokuanke.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.backend.ocr.SudokuReader
import com.backend.ocr.SudokuReaderUtil
import com.google.mlkit.vision.common.InputImage
import com.sudokuanke.R
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

    private fun findLoadedImages(): ArrayList<String> {
        val dir: File = File(applicationContext.getFilesDir(), "images")
        if (!dir.exists() || !dir.isDirectory){
            return ArrayList<String>()
        }

        var fileNames = ArrayList<String>()
        val files = dir.listFiles()
        files?.forEach { file ->
            fileNames.add(file.name)
        }

        return fileNames
    }

    private var photoUriSet: Boolean = false;
    private lateinit var photoUri: Uri;
    private lateinit var imageView: ImageView;
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                if (photoUri == null) {
                    Log.e("empty uri", "")
                    return@registerForActivityResult
                }
                imageView.setImageURI(photoUri)
            } else {
                Log.e("Error", "Take picture unsuccessful")
            }
        }

    private fun startCameraCapture() {
        val photoFile = createImageFile()
        this.imageView =  findViewById<ImageView>(R.id.image_view)
        Log.e("PACKAGENAME", "${packageName}")
        this.photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            photoFile
        )
        this.photoUriSet = true;

        takePictureLauncher.launch(photoUri)
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
            Log.e("Sudoku", "photoUri not initialized")
            return
        }

        Log.e("Sudoku", "Calling SudokuReader")
        val image = InputImage.fromFilePath(applicationContext, photoUri)
        sudokuReader.setImage(image)
        sudokuReader.readSudoku() { sudoku ->
            val prettyPrint = SudokuReaderUtil.prettyPrintSudoku(sudoku)
            Log.e("Sudoku", prettyPrint)
            AlertDialog.Builder(this)
                .setTitle("Read Sudoku")
                .setMessage(prettyPrint)
                .setNeutralButton("OK", null)
                .show()
        }
    }
}
