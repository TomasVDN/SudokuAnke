package com.frontend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.core.view.size

class NumberSelector(context: Context, attrs: AttributeSet? = null) : GridLayout(context, attrs) {

    var onDigitSelected: ((Int) -> Unit)? = null

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.BLACK
    }

    private val paintText = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 36f
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.BLACK
    }

    init {
        rowCount = 1
        columnCount = 10
        setWillNotDraw(false) // important: forces onDraw to be called on ViewGroups

        repeat(rowCount * columnCount) { index ->
            val cell = NumberButton(context).apply {
                value = null
                setOnClickListener { onCellClicked(index) }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(index, 1f)
                rowSpec = GridLayout.spec(0, 1f)
            }
            addView(cell, params)
        }
    }

    private fun onCellClicked(index: Int) {
        // deselect all, select clicked
        for (i in 0 until childCount) {
            (getChildAt(i) as NumberButton).isCellSelected = (i == index)
            getChildAt(i).invalidate()
        }

        onDigitSelected?.invoke(index)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellW = width / columnCount.toFloat()
        val cellH = height.toFloat()

        for (i in 0..columnCount) {
            canvas.drawLine(i * cellW, 0f, i * cellW, height.toFloat(), paint)
            // horizontal lines
            canvas.drawLine(0f, i * cellH, width.toFloat(), i * cellH, paint)

            val text =  if (i == 0) "X" else i.toString();
            canvas.drawText(text, i * cellW + cellW / 2f, 2 * cellH / 3f, paintText)
        }
    }
}