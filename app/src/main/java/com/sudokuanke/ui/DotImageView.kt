package com.sudokuanke.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView

class DotImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private val dots = mutableListOf<PointF>()
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            dots.add(PointF(event.x, event.y))
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (dot in dots) {
            canvas.drawCircle(dot.x, dot.y, 15f, paint)
        }
    }
}
