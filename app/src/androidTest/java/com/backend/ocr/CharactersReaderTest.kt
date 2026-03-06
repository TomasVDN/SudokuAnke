package com.backend.ocr

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.backend.ocr.CharactersReader
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CharactersReaderTest {
    @Test
    fun readText() {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        val inputStream = testContext.assets.open("sudoku1.jpg")
        val tempFile = File(targetContext.cacheDir, "temp_sudoku1.jpg")

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val uri = Uri.fromFile(tempFile)
        val image = InputImage.fromFilePath(testContext, uri)

        assert(tempFile.exists())

        val charactersReader = CharactersReader()
        charactersReader.setImage(image)
        val task : Task<Text> = charactersReader.readText()
        val result: Text = Tasks.await<Text>(task)

        assert(result.text.isNotEmpty())

        charactersReader.handleText(result)
    }
}