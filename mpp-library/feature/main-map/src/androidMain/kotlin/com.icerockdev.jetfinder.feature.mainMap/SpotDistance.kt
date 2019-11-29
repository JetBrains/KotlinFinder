package com.icerockdev.jetfinder.feature.mainMap

import android.animation.*
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Property
import android.view.View
import kotlin.random.Random

class SpotDistance(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val initialBarHeight = 5.0f
    private val maxScale: Float = 6.0f
    private val barsCount: Int = 9
    private var initialBarWidth = 0.0
    private val bars = MutableList(barsCount) { i -> Bar(i) }
    private val density = resources.displayMetrics.density

    var distance: Float = 0.0f
        set(value) {
            bars.forEach { it.isDistanceChanged = true }
            field = value
        }

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = context.resources.getColor(R.color.orange)
    }

    private val shadowPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = context.resources.getColor(R.color.orange)
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
        alpha = 70
    }

    private fun start() {
        bars.forEachIndexed { index, bar ->
            bar.animator.repeatCount = ValueAnimator.INFINITE
            bar.animator.repeatMode = ValueAnimator.REVERSE
            bar.animator.setFloatValues(bar.thisHeight, bar.newHeight())
            bar.animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                    if (animation?.isRunning != true || bar.isDistanceChanged) {
                        bar.animator.duration = bar.getDuration()
                        val height = bar.newHeight()
                        val bottomHeight = height - 10f
                        bar.animator.setFloatValues(
                            if (bottomHeight > initialBarHeight) bottomHeight else initialBarHeight,
                            height
                        )
                        bar.isDistanceChanged = false
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {}

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })

            if (index == barsCount - 1)
                bar.animator.addUpdateListener {
                    invalidate()
                }
            bar.animator.start()
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (initialBarWidth > 0)
            canvas?.apply {
                bars.forEach {
                    drawRoundRect(
                        it.left - 10,
                        it.top - 5,
                        it.right + 10,
                        it.bottom + 5,
                        10f,
                        10f,
                        shadowPaint
                    )
                    drawRoundRect(
                        it.left,
                        it.top,
                        it.right,
                        it.bottom,
                        10f,
                        10f,
                        paint
                    )
                }
            }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        if (initialBarWidth == 0.0) {
            initialBarWidth = w / barsCount / 2.0
            start()
        }
        setMeasuredDimension(w, h)
    }

    inner class Bar(val idx: Int) {
        var thisHeight = initialBarHeight
        val left
            get() = (2 * initialBarWidth * idx).toFloat()

        val right
            get() = (left + initialBarWidth).toFloat()

        val top
            get() = ((initialBarHeight * maxScale + 10) - thisHeight) / 2 * density

        val bottom
            get() = top + (thisHeight * density)

        val property =
            object : Property<SpotDistance, Float>(Float::class.java, "barHeight") {
                override fun set(view: SpotDistance, value: Float?) {
                    thisHeight = value ?: initialBarHeight
                }

                override fun get(view: SpotDistance): Float? {
                    return thisHeight
                }
            }

        val animator = ObjectAnimator.ofFloat(
            this@SpotDistance,
            property,
            initialBarHeight,
            initialBarHeight * maxScale
        )

        fun newHeight(): Float {
            var scaleFactor =
                Random.nextFloat() * 1.0f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat() +
                        distance * maxScale

            if (scaleFactor < 0.0 || distance == 0.0f)
                scaleFactor = 1.0f
            thisHeight = initialBarHeight * scaleFactor
            return thisHeight
        }

        fun getDuration(): Long {
            var duration =
                0.1 + Random.nextFloat() * (0.1f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat())

            if (duration < 0.1)
                duration = 0.2
            return (duration * 2000).toLong()
        }

        var isDistanceChanged = false

    }
}