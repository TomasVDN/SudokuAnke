package com.frontend

import android.content.Context
import android.graphics.Canvas
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
        color = ColorPrefs.getColor(context, ColorPrefs.DIGIT_FIXED)
    }
    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 50f
    }
    private val highlightPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ColorPrefs.getColor(context, ColorPrefs.CELL_HIGHLIGHT)
        alpha = 50
    }

    override fun onDraw(canvas: Canvas) {
        if (isCellSelected && !isFixed) canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), highlightPaint)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)

        value?.let {
            textPaint.color = when {
                hasConflict -> ColorPrefs.getColor(context, ColorPrefs.DIGIT_CONFLICT)
                isFixed     -> ColorPrefs.getColor(context, ColorPrefs.DIGIT_FIXED)
                else        -> ColorPrefs.getColor(context, ColorPrefs.DIGIT_USER)
            }
            canvas.drawText(it.toString(), width / 2f, height / 2f - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        }
    }
}
