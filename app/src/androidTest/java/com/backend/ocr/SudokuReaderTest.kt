package com.backend.ocr

import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.backend.ocr.SudokuReader
import com.backend.ocr.SudokuReaderUtil
import com.google.mlkit.vision.common.InputImage
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SudokuReaderTest {
    fun readSudoku(fileName: String) {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        val inputStream = testContext.assets.open(fileName)
        val tempFile = File(targetContext.cacheDir, "temp_" + fileName)

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        assert(tempFile.exists())
        assert(tempFile.length() > 0)

        val uri = Uri.fromFile(tempFile)
        val image = InputImage.fromFilePath(testContext, uri)

        val sudokuReader = SudokuReader()
        sudokuReader.setImage(image)

        val latch = CountDownLatch(1)
        var sudoku: Array<IntArray>? = null;
        sudokuReader.readSudoku() { callback ->
            sudoku = callback
            latch.countDown()
        }

        latch.await(3, TimeUnit.SECONDS)

        assert(sudoku != null)

        Log.d("Sudoku", SudokuReaderUtil.prettyPrintSudoku(sudoku))

        assert(sudoku?.isNotEmpty() == true)
    }

    @Test
    fun readSudokus() {
        readSudoku("sudoku1.jpg")
        readSudoku("sudoku2.jpg")
        readSudoku("sudoku3_angle.jpg")
        readSudoku("sudoku4_paper_good_quality.jpg")
        readSudoku("sudoku5_paper_filled_in.jpg")
        readSudoku("sudoku6_paper_poor_quality.jpg")
    }

    @Test
    fun readSudoku4(){
        readSudoku("sudoku4_paper_good_quality.jpg")
    }
}