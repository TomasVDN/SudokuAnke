package com.frontend

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.withClip
import com.sudokuanke.R

class ShinyButton(context: Context, attrs: AttributeSet? = null) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    private val shineEnabled: Boolean
    private var animator: ValueAnimator? = null
    private var gradientOffset = 0f

    private val shinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var shineGradient: LinearGradient? = null
    private val shineMatrix = Matrix()
    private val clipPath = Path()
    private val cornerRadius = 8f * context.resources.displayMetrics.density

    init {
        shineEnabled = ColorPrefs.getShineEnabled(context)
        setTextColor(ContextCompat.getColor(context, R.color.button_text))
        gravity = android.view.Gravity.CENTER

        if (shineEnabled) {
            background = null
        } else {
            val shape = GradientDrawable().apply {
                setColor(ColorPrefs.getColor(context, ColorPrefs.BUTTON_BG))
                cornerRadius = this@ShinyButton.cornerRadius
            }
            background = RippleDrawable(
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_ripple)),
                shape,
                null
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!shineEnabled || w == 0) return

        val c1 = ColorPrefs.getColor(context, ColorPrefs.BUTTON_SHINE_1)
        val colors = intArrayOf(
            c1,
            ColorPrefs.getColor(context, ColorPrefs.BUTTON_SHINE_2),
            ColorPrefs.getColor(context, ColorPrefs.BUTTON_SHINE_3),
            c1,
        )
        shineGradient = LinearGradient(0f, 0f, w.toFloat(), 0f, colors, null, Shader.TileMode.REPEAT)
        shinePaint.shader = shineGradient

        clipPath.reset()
        clipPath.addRoundRect(RectF(0f, 0f, w.toFloat(), h.toFloat()), cornerRadius, cornerRadius, Path.Direction.CW)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!shineEnabled) return
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener {
                gradientOffset = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
        animator = null
    }

    override fun onDraw(canvas: Canvas) {
        if (shineEnabled) {
            canvas.withClip(clipPath) {
                shineMatrix.setTranslate(gradientOffset * width.toFloat(), 0f)
                shineGradient?.setLocalMatrix(shineMatrix)
                drawRect(0f, 0f, width.toFloat(), height.toFloat(), shinePaint)
            }
        }
        super.onDraw(canvas)
    }
}
