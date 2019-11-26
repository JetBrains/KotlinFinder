package com.icerockdev.jetfinder.feature.spotSearch

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TypeConverter
import android.animation.TypeEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Property
import android.view.View
import kotlin.random.Random

class SpotDistance(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val initialBarHeight = 10.0f
    private val maxScale: Float = 8.0f
    private val barsCount: Int = 20
    private var initialBarWidth = 0.0
    private val bars = MutableList(barsCount) { i -> Bar(i) }
    private val density = resources.displayMetrics.density

    private var distance: Float = 0.0f

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = context.resources.getColor(R.color.orange)

    }

    init {

    }

    private val property =
        object : Property<SpotDistance, BarHeightList>(BarHeightList::class.java, "barHeight") {
            override fun set(view: SpotDistance, value: BarHeightList?) {
                value?.values?.forEachIndexed { index, height -> bars[index].thisHeight = height }
            }

            override fun get(view: SpotDistance): BarHeightList? {
                return thisHeight
            }
        }

    val aset: AnimatorSet = AnimatorSet()

    val animator = ObjectAnimator.ofObject(this, property, object: TypeEvaluator<BarHeightList> {
        override fun evaluate(
            fraction: Float,
            startValue: BarHeightList?,
            endValue: BarHeightList?
        ): BarHeightList {



            return BarHeightList(endValue?.values ?: emptyList())
        }

    }, emptyList<BarHeightList>() )

//    val animator = ObjectAnimator.ofFloat(
//        this,
//        property,
//        initialBarHeight,
//        initialBarHeight * maxScale
//    )


    fun update(distance: Float) {
        this.distance = distance
        val oldHeights = arrayListOf<Float>()

        bars.forEachIndexed { index, bar ->
            var scaleFactor =
                Random.nextFloat() * 1.0f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat() +
                        this.distance * this.maxScale

            if (scaleFactor < 0.0 || this.distance == 0.0f)
                scaleFactor = 0.0f

            var duration =
                0.1 + Random.nextFloat() * (0.1f * (if (Random.nextInt() % 2 == 0) 1 else -1).toFloat())

            if (duration < 0.1)
                duration = 0.2
            oldHeights[index] = bar.thisHeight
            val newHeight = initialBarHeight * scaleFactor


            //todo addUpdateListener


            //bar.thisHeight = initialBarHeight * scaleFactor
//            it.animator.duration = duration.toLong()
//            it.animator.setFloatValues(oldHeight, newHeight)
//            it.animator.set
//            it.animator.start()
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (initialBarWidth > 0)
            canvas?.apply {
                bars.forEach { drawRect(it.left, it.top, it.right, it.bottom, paint) }
            }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = View.resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = View.resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )

        initialBarWidth = w / barsCount / 2.0
        setMeasuredDimension(w, h)
    }

    inner class Bar(val idx: Int) {
        var thisHeight = initialBarHeight
        val left
            get() = (2 * initialBarWidth * idx).toFloat()

        val right
            get() = (left + initialBarWidth).toFloat()

        val top
            get() = ((initialBarHeight * maxScale) - thisHeight) / 2 * density

        val bottom
            get() = top + (thisHeight * density)

    }

    data class BarHeightList(val values: List<Float>)
}