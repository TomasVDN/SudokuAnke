package com.sudokuanke.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.values

class DotImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    public val dots = mutableListOf<PointF>()
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun getScale(): Float {
        return imageMatrix.values().get(0);
    }

    fun getDotsInBitmapCoordinates() : List<PointF> {
        val imgMatrix = imageMatrix
        val inverse = Matrix()

        val dotsBitmapCoordinates = mutableListOf<PointF>()

        if (imgMatrix.invert(inverse)) {
            for (i in 0..<dots.size) {
                val point = dots[i]
                val touchPoint = floatArrayOf(point.x, point.y)
                inverse.mapPoints(touchPoint)

                val bitmapX = touchPoint[0]
                val bitmapY = touchPoint[1]

                dotsBitmapCoordinates.add(PointF(bitmapX, bitmapY))

                Log.d("DotImageView", "Event moudse: ($point.x, $point.y)")
                Log.d("DotImageView", "Bitmap view pixel touched: ($bitmapX, $bitmapY)")
            }
        }

        return dotsBitmapCoordinates
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            dots.add(PointF(event.x, event.y))
            if (dots.size > 4) {
                dots.clear()
            }
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
