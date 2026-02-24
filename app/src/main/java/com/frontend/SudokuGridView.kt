package com.frontend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import com.backend.Sudoku

class SudokuGridView(context: Context, attrs: AttributeSet? = null) : GridLayout(context, attrs) {

    private lateinit var sudoku: Sudoku
    private lateinit var undoer: Undoer

    var checkValidity: (() -> Unit)? = null

    private var selectedDigit = -1

    fun setSudoku(sudoku: Sudoku) {
        this.sudoku = sudoku
        refreshValues()
    }

    fun setUndoer(undoer: Undoer) {
        this.undoer = undoer
    }

    fun setSelectedDigit(selectedDigit: Int) {
        this.selectedDigit = selectedDigit
        refreshValues()
    }

    fun refreshValues() {
        for (i in 0 until childCount) {
            val cell = getChildAt(i) as SudokuCellView
            val row = i / columnCount
            val col = i % columnCount
            val digit = sudoku.getDigitAt(row, col)
            cell.value = if (digit == 0) null else digit
            cell.invalidate()
        }
    }

    private val thinPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.BLACK
    }
    private val thickPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.BLACK
    }

    init {
        rowCount = 9
        columnCount = 9
        setWillNotDraw(false) // important: forces onDraw to be called on ViewGroups

        repeat(rowCount * columnCount) { index ->
            val cell = SudokuCellView(context).apply {
                isFixed = false
                value = null
                setOnClickListener { onCellClicked(index) }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(index % columnCount, 1f)
                rowSpec = GridLayout.spec(index / columnCount, 1f)
            }
            addView(cell, params)
        }
    }

    private fun onCellClicked(index: Int) {
        // deselect all, select clicked
        for (i in 0 until childCount) {
            (getChildAt(i) as SudokuCellView).isCellSelected = (i == index)
            getChildAt(i).invalidate()
        }

        if (selectedDigit == -1)
        {
            return
        }

        val row = index / columnCount
        val col = index % columnCount

        val previousDigit = sudoku.getDigitAt(row, col)
        undoer.addPlay(row, col, previousDigit, sudoku.isValid)

        sudoku.place(row, col, selectedDigit)
        refreshValues()
        checkValidity?.invoke()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellW = width / columnCount.toFloat()
        val cellH = height / rowCount.toFloat()

        for (i in 0..columnCount) {
            val paint = if (i % 3 == 0) thickPaint else thinPaint
            // vertical lines
            canvas.drawLine(i * cellW, 0f, i * cellW, height.toFloat(), paint)
            // horizontal lines
            canvas.drawLine(0f, i * cellH, width.toFloat(), i * cellH, paint)
        }
    }
}