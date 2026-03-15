package com.sudokuanke.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.backend.ocr.SudokuReader
import com.backend.ocr.TranslationApplier
import com.google.mlkit.vision.common.InputImage
import com.sudokuanke.R
import com.sudokuanke.ui.DotImageView
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
    private lateinit var imageView: DotImageView
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageView.setImageURI(photoUri)
            } else {
                Log.e("Error", "Take picture unsuccessful")
            }
        }

    private fun startCameraCapture() {
        imageView = findViewById<DotImageView>(R.id.image_view)
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
            val imageTranslated = withContext(Dispatchers.IO) {
                val image = InputImage.fromFilePath(applicationContext, photoUri)
                val imageTranslated = getTranslatedImage(image)
                imageTranslated
               // sudokuReader.setImage(imageTranslated)
            }

            imageView.setImageBitmap(imageTranslated.bitmapInternal)


            progressBar.visibility = View.GONE
            imageView.visibility = View.VISIBLE
        //    sudokuReader.readSudoku() { sudoku ->
        //        progressBar.visibility = View.GONE
        //        //startInsertActivity(SudokuUtil.toString(sudoku))
        //    }
        }
    }

    private fun getFloatArrayDots(): FloatArray? {
        val dots = imageView.getDotsInBitmapCoordinates()
        if (dots.size == 4) {
            val floatArray = FloatArray(dots.size * 2)
            for (i in dots.indices) {
                floatArray[i * 2] = dots[i].y
                floatArray[i * 2 + 1] = dots[i].x
            }
            return floatArray
        }
        return null
    }
    private fun getTranslatedImage(inputImage: InputImage): InputImage {
        val dots = getFloatArrayDots() ?: return inputImage

        val inputBitmap = inputImage.bitmapInternal
        val outputBitmap = TranslationApplier.translateImage(inputBitmap, dots)
        return InputImage.fromBitmap(outputBitmap, 0)
    }

    private fun startInsertActivity(boardAsString : String) {
        val intent = Intent(this, InsertActivity::class.java)
        intent.putExtra("board", boardAsString)
        finish()
        startActivity(intent)
    }
}
