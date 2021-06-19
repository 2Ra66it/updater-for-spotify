package ru.ra66it.updaterforspotify.presentation.ui.customview.swiperefresh

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


const val MAX_ARC_DEGREE = 330
const val ANIMATION_DURATION = 888
const val DEFAULT_START_DEGREES = 285
const val DEFAULT_STROKE_WIDTH = 3

class RefreshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IRefreshStatus {

    private val rect = RectF()
    private val paint = Paint()

    private var startDegrees: Float = 0.toFloat()
    private var swipeDegrees: Float = 0.toFloat()

    private var strokeWidth: Float = 0.toFloat()

    private var hasTriggeredRefresh: Boolean = false

    private var rotateAnimator: ValueAnimator? = null

    init {
        initData()
        initPaint()
    }

    private fun initData() {
        val density = resources.displayMetrics.density
        strokeWidth = density * DEFAULT_STROKE_WIDTH

        startDegrees = DEFAULT_START_DEGREES.toFloat()
        swipeDegrees = 0.0f
    }

    private fun initPaint() {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = Color.parseColor("#1ed760")
    }

    private fun startAnimator() {
        rotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        rotateAnimator?.interpolator = LinearInterpolator()
        rotateAnimator?.addUpdateListener { animation ->
            val rotateProgress = animation.animatedValue as Float
            setStartDegrees(DEFAULT_START_DEGREES + rotateProgress * 360)
        }
        rotateAnimator?.repeatMode = ValueAnimator.RESTART
        rotateAnimator?.repeatCount = ValueAnimator.INFINITE
        rotateAnimator?.duration = ANIMATION_DURATION.toLong()

        rotateAnimator?.start()
    }

    private fun resetAnimator() {
        rotateAnimator?.cancel()
        rotateAnimator?.removeAllUpdateListeners()

        rotateAnimator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawArc(canvas)
    }

    override fun onDetachedFromWindow() {
        resetAnimator()
        super.onDetachedFromWindow()
    }

    private fun drawArc(canvas: Canvas) {
        canvas.drawArc(rect, startDegrees, swipeDegrees, false, paint)
    }

    private fun setStartDegrees(startDegrees: Float) {
        this.startDegrees = startDegrees
        postInvalidate()
    }

    fun setSwipeDegrees(swipeDegrees: Float) {
        this.swipeDegrees = swipeDegrees
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val radius = Math.min(w, h) / 2.0f
        val centerX = w / 2.0f
        val centerY = h / 2.0f

        rect.set(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius
        )
        rect.inset(strokeWidth / 2.0f, strokeWidth / 2.0f)
    }

    override fun reset() {
        resetAnimator()

        hasTriggeredRefresh = false
        startDegrees = DEFAULT_START_DEGREES.toFloat()
        swipeDegrees = 0.0f
    }

    override fun refreshing() {
        hasTriggeredRefresh = true
        swipeDegrees = MAX_ARC_DEGREE.toFloat()

        startAnimator()
    }

    override fun refreshComplete() {
    }

    override fun pullToRefresh() {
    }

    override fun releaseToRefresh() {}

    override fun pullProgress(pullDistance: Float, pullProgress: Float) {
        if (!hasTriggeredRefresh) {
            val swipeProgress = Math.min(1.0f, pullProgress)
            setSwipeDegrees(swipeProgress * MAX_ARC_DEGREE)
        }
    }

}

interface IRefreshStatus {

    fun reset()

    fun refreshing()

    fun refreshComplete()

    fun pullToRefresh()

    fun releaseToRefresh()

    fun pullProgress(pullDistance: Float, pullProgress: Float)
}