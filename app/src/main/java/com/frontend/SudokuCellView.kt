package com.frontend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SudokuCellView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var value: Int? = null
    var isCellSelected: Boolean = false
    var isFixed: Boolean = false
    var hasConflict: Boolean = false

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.BLACK
    }
    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 50f
    }
    private val highlightPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
        alpha = 50
    }

    override fun onDraw(canvas: Canvas) {
        // Background
        if (isCellSelected) canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), highlightPaint)

        // Border
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)

        // Text
        value?.let {
            textPaint.color = when {
                hasConflict -> Color.RED
                isFixed -> Color.BLACK
                else -> Color.BLUE
            }
            canvas.drawText(it.toString(), width / 2f, height / 2f - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        }
    }
}