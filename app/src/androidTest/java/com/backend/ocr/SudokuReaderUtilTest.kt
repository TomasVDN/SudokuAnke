package com.backend.ocr

import android.graphics.Rect
import com.backend.ocr.SudokuReaderUtil
import org.junit.Test

class SudokuReaderUtilTest {
    @Test
    fun indexForBoundingBox_isCorrectOnTrivialExample() {
        val totalBoundingBox = Rect(0, 0, 9, 9)
        val boundingBox00 = Rect(0, 0, 1, 1)
        val boundingBox23 = Rect(2, 3, 3, 4)
        val boundingBox08 = Rect(0, 8, 1, 9)

        val indices00 = SudokuReaderUtil.indexForBoundingBox(totalBoundingBox, boundingBox00)
        val indices23 = SudokuReaderUtil.indexForBoundingBox(totalBoundingBox, boundingBox23)
        val indices08 = SudokuReaderUtil.indexForBoundingBox(totalBoundingBox, boundingBox08)

        assert(indices00[0] == 0)
        assert(indices00[1] == 0)
        assert(indices23[0] == 2)
        assert(indices23[1] == 3)
        assert(indices08[0] == 0)
        assert(indices08[1] == 8)
    }

    @Test
    fun indexForBoundingBox_isCorrect() {
        val width = 80
        val height = 50
        val xOffset = 8
        val yOffset = 150
        val totalBoundingBox = Rect(xOffset, yOffset, xOffset+width, yOffset + height)
        val boundingBox46 = Rect(xOffset + 4*width/9, yOffset+6*height/9, xOffset +5*width/9, yOffset + 7*height/9)

        val indices46 = SudokuReaderUtil.indexForBoundingBox(totalBoundingBox, boundingBox46)

        assert(indices46[0] == 4)
        assert(indices46[1] == 6)
    }

    @Test
    fun indexForBoundingBox_isCorrectWhenFuzzy() {
        val width = 80
        val height = 50
        val xOffset = 8
        val yOffset = 150
        val totalBoundingBox = Rect(xOffset, yOffset, xOffset+width, yOffset + height)
        val boundingBox46 = Rect(xOffset + 4*width/9 + 5, yOffset+6*height/9 - 3, xOffset +5*width/9 + 5, yOffset + 7*height/9 + 5)

        val indices46 = SudokuReaderUtil.indexForBoundingBox(totalBoundingBox, boundingBox46)

        assert(indices46[0] == 4)
        assert(indices46[1] == 6)
    }
}