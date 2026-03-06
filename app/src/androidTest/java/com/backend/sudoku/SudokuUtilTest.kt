package com.backend.sudoku

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.backend.sudoku.SudokuGenerator
import com.backend.sudoku.SudokuUtil
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SudokuUtilTest {
    @Test
    @Throws(IOException::class)
    fun writeAndReadToFile() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val generator = SudokuGenerator()
        val board = generator.generate(SudokuGenerator.Difficulty.EASY)

        var fileName = "helloWorld"
        SudokuUtil.saveToDisk(appContext, fileName, board)
        val result = SudokuUtil.getFromDisk(appContext, fileName)

        Assert.assertArrayEquals(board, result)
    }

    // TODO[Tomas] add a test for illegal chars
}