package com.frontend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class NumberButton(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    var value: Int? = null
    var isCellSelected: Boolean = false

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = ColorPrefs.getColor(context, ColorPrefs.DIGIT_FIXED)
    }
    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 50f
        color = ColorPrefs.getColor(context, ColorPrefs.DIGIT_FIXED)
    }
    private val highlightPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ColorPrefs.getColor(context, ColorPrefs.NUMBER_SELECTED_BACKGROUND)
        alpha = 30
    }

    override fun onDraw(canvas: Canvas) {
        if (isCellSelected) canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), highlightPaint)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)

        value?.let {
            canvas.drawText(it.toString(), width / 2f, height / 2f - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        }
    }
}
