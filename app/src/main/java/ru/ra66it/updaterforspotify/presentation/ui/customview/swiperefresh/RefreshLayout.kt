package ru.ra66it.updaterforspotify.presentation.ui.customview.swiperefresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import androidx.annotation.NonNull
import androidx.core.view.*
import ru.ra66it.updaterforspotify.BuildConfig

const val INVALID_INDEX = -1
const val INVALID_POINTER = -1
//the default height of the RefreshView
const val DEFAULT_REFRESH_SIZE_DP = 30
//the animation duration of the RefreshView scroll to the refresh point or the start point
const val DEFAULT_ANIMATE_DURATION = 300
// the threshold of the trigger to refresh
const val DEFAULT_REFRESH_TARGET_OFFSET_DP = 40
const val DECELERATE_INTERPOLATION_FACTOR = 2.0f

class RefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs), NestedScrollingParent, NestedScrollingChild {

    // NestedScroll
    private var totalUnconsumed: Float = 0.toFloat()
    private var nestedScrollInProgress: Boolean = false
    private val parentScrollConsumed = IntArray(2)
    private val parentOffsetInWindow = IntArray(2)
    private val nestedScrollingChildHelper: NestedScrollingChildHelper
    private val nestedScrollingParentHelper: NestedScrollingParentHelper

    //whether to remind the callback listener(OnRefreshListener)
    private var isAnimatingToStart: Boolean = false
    private var isRefreshing: Boolean = false
    private var isFitRefresh: Boolean = false
    private var isBeingDragged: Boolean = false
    private var notifyListener: Boolean = false
    private var dispatchTargetTouchDown: Boolean = false

    private var refreshViewIndex = INVALID_INDEX
    private var activePointerId = INVALID_POINTER
    private var animateToStartDuration = DEFAULT_ANIMATE_DURATION
    private var animateToRefreshDuration = DEFAULT_ANIMATE_DURATION

    private var from: Int = 0
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private val refreshViewSize: Int

    private var initialDownY: Float = 0.toFloat()
    private var initialScrollY: Float = 0.toFloat()
    private var initialMotionY: Float = 0.toFloat()
    private var currentTouchOffsetY: Float = 0.toFloat()
    private var targetOrRefreshViewOffsetY: Float = 0.toFloat()
    private var refreshInitialOffset: Float = 0.toFloat()
    private var refreshTargetOffset: Float = 0.toFloat()

    // Whether the client has set a custom refreshing position;
    private var usingCustomRefreshTargetOffset = false
    // Whether the client has set a custom starting position;
    private var usingCustomRefreshInitialOffset = false
    // Whether or not the RefreshView has been measured.
    private var refreshViewMeasured = false

    private var refreshStyle = RefreshStyle.NORMAL

    private var target: View? = null
    private var refreshView: View? = null

    private var dragDistanceConverter: IDragDistanceConverter? = null

    private var refreshStatus: IRefreshStatus? = null
    var onRefreshListener: (() -> Unit)? = null

    private var animateToStartInterpolator: Interpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
    private var animateToRefreshInterpolator: Interpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)

    private val animateToRefreshingAnimation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            when (refreshStyle) {
                RefreshStyle.FLOAT -> {
                    val refreshTargetOffset = this@RefreshLayout.refreshTargetOffset + refreshInitialOffset
                    animateToTargetOffset(refreshTargetOffset, refreshView!!.top.toFloat(), interpolatedTime)
                }
                else -> animateToTargetOffset(this@RefreshLayout.refreshTargetOffset, target!!.top.toFloat(), interpolatedTime)
            }
        }
    }

    private val animateToStartAnimation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            when (refreshStyle) {
                RefreshStyle.FLOAT -> animateToTargetOffset(refreshInitialOffset, refreshView!!.top.toFloat(), interpolatedTime)
                else -> animateToTargetOffset(0.0f, target!!.top.toFloat(), interpolatedTime)
            }
        }
    }

    private val refreshingListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            isAnimatingToStart = true
            refreshStatus?.refreshing()
        }

        override fun onAnimationRepeat(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
            if (notifyListener) {
                onRefreshListener?.invoke()
            }

            isAnimatingToStart = false
        }
    }

    private val resetListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            isAnimatingToStart = true
            refreshStatus?.refreshComplete()
        }

        override fun onAnimationRepeat(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
            reset()
        }
    }

    private val targetOrRefreshViewTop: Int
        get() {
            return when (refreshStyle) {
                RefreshStyle.FLOAT -> refreshView!!.top
                else -> target!!.top
            }
        }

    private val targetOrRefreshViewOffset: Int
        get() {
            return when (refreshStyle) {
                RefreshStyle.FLOAT -> (refreshView!!.top - refreshInitialOffset).toInt()
                else -> target!!.top
            }
        }

    private val isTargetValid: Boolean
        get() {
            for (i in 0 until childCount) {
                if (target === getChildAt(i)) {
                    return true
                }
            }

            return false
        }

    private fun animateToTargetOffset(targetEnd: Float, currentOffset: Float, interpolatedTime: Float) {
        val targetOffset = (from + (targetEnd - from) * interpolatedTime).toInt()

        setTargetOrRefreshViewOffsetY((targetOffset - currentOffset).toInt())
    }

    init {
        val metrics = resources.displayMetrics
        refreshViewSize = (DEFAULT_REFRESH_SIZE_DP * metrics.density).toInt()

        refreshTargetOffset = DEFAULT_REFRESH_TARGET_OFFSET_DP * metrics.density

        targetOrRefreshViewOffsetY = 0.0f
        refreshInitialOffset = 0.0f

        nestedScrollingParentHelper = NestedScrollingParentHelper(this)
        nestedScrollingChildHelper = NestedScrollingChildHelper(this)

        initRefreshView()
        initDragDistanceConverter()
        isNestedScrollingEnabled = true
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)
    }

    override fun onDetachedFromWindow() {
        reset()
        clearAnimation()
        super.onDetachedFromWindow()
    }

    private fun reset() {
        setTargetOrRefreshViewToInitial()

        currentTouchOffsetY = 0.0f

        refreshStatus?.reset()
        refreshView?.visibility = View.GONE

        isRefreshing = false
        isAnimatingToStart = false
    }

    private fun setTargetOrRefreshViewToInitial() {
        when (refreshStyle) {
            RefreshStyle.FLOAT -> setTargetOrRefreshViewOffsetY((refreshInitialOffset - targetOrRefreshViewOffsetY).toInt())
            else -> setTargetOrRefreshViewOffsetY((0 - targetOrRefreshViewOffsetY).toInt())
        }
    }

    private fun initRefreshView() {
        refreshView = RefreshView(context)
        refreshView?.visibility = View.GONE
        if (refreshView is IRefreshStatus) {
            refreshStatus = refreshView as IRefreshStatus?
        } else {
            throw ClassCastException("the refreshView must implement the interface IRefreshStatus")
        }

        val layoutParams = LayoutParams(refreshViewSize, refreshViewSize)
        addView(refreshView, layoutParams)
    }

    private fun initDragDistanceConverter() {
        dragDistanceConverter = MaterialDragDistanceConverter()
    }

    /**
     * @param refreshView  must implements the interface IRefreshStatus
     * @param layoutParams the with is always the match_parent， no matter how you set
     * the height you need to set a specific value
     */
    fun setRefreshView(@NonNull refreshView: View?, layoutParams: ViewGroup.LayoutParams) {
        if (refreshView == null) {
            throw NullPointerException("the refreshView can't be null")
        }

        if (this.refreshView === refreshView) {
            return
        }

        if (this.refreshView != null && this.refreshView?.parent != null) {
            (this.refreshView?.parent as ViewGroup).removeView(this.refreshView)
        }

        if (refreshView is IRefreshStatus) {
            refreshStatus = refreshView
        } else {
            throw ClassCastException("the refreshView must implement the interface IRefreshStatus")
        }
        refreshView.visibility = View.GONE
        addView(refreshView, layoutParams)

        this.refreshView = refreshView
    }

    fun setDragDistanceConverter(@NonNull dragDistanceConverter: IDragDistanceConverter?) {
        if (dragDistanceConverter == null) {
            throw NullPointerException("the dragDistanceConverter can't be null")
        }
        this.dragDistanceConverter = dragDistanceConverter
    }

    /**
     * @param animateToStartInterpolator The interpolator used by the animation that
     * move the refresh view from the refreshing point or
     * (the release point) to the start point.
     */
    fun setAnimateToStartInterpolator(@NonNull animateToStartInterpolator: Interpolator?) {
        if (animateToStartInterpolator == null) {
            throw NullPointerException("the animateToStartInterpolator can't be null")
        }

        this.animateToStartInterpolator = animateToStartInterpolator
    }

    /**
     * @param animateToRefreshInterpolator The interpolator used by the animation that
     * move the refresh view the release point to the refreshing point.
     */
    fun setAnimateToRefreshInterpolator(@NonNull animateToRefreshInterpolator: Interpolator?) {
        if (animateToRefreshInterpolator == null) {
            throw NullPointerException("the animateToRefreshInterpolator can't be null")
        }

        this.animateToRefreshInterpolator = animateToRefreshInterpolator
    }

    /**
     * @param animateToStartDuration The duration used by the animation that
     * move the refresh view from the refreshing point or
     * (the release point) to the start point.
     */
    fun setAnimateToStartDuration(animateToStartDuration: Int) {
        this.animateToStartDuration = animateToStartDuration
    }

    /**
     * @param animateToRefreshDuration The duration used by the animation that
     * move the refresh view the release point to the refreshing point.
     */
    fun setAnimateToRefreshDuration(animateToRefreshDuration: Int) {
        this.animateToRefreshDuration = animateToRefreshDuration
    }

    /**
     * @param refreshTargetOffset The minimum distance that trigger refresh.
     */
    fun setRefreshTargetOffset(refreshTargetOffset: Float) {
        this.refreshTargetOffset = refreshTargetOffset
        usingCustomRefreshTargetOffset = true
        requestLayout()
    }

    /**
     * @param refreshInitialOffset the top position of the [.refreshView] relative to its parent.
     */
    fun setRefreshInitialOffset(refreshInitialOffset: Float) {
        this.refreshInitialOffset = refreshInitialOffset
        usingCustomRefreshInitialOffset = true
        requestLayout()
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return when (refreshStyle) {
            RefreshStyle.FLOAT -> when {
                refreshViewIndex < 0 -> i
                i == childCount - 1 -> refreshViewIndex // Draw the selected child last
                i >= refreshViewIndex -> i + 1 // Move the children after the selected child earlier one
                else -> i // Keep the children before the selected child the same
            }
            else -> when {
                refreshViewIndex < 0 -> i
                i == 0 -> refreshViewIndex // Draw the selected child first
                i <= refreshViewIndex ->  i - 1 // Move the children before the selected child earlier one
                else -> i
            }
        }
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if (target is AbsListView || target != null && !ViewCompat.isNestedScrollingEnabled(target!!)) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }

    // NestedScrollingParent
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return when (refreshStyle) {
            RefreshStyle.FLOAT -> (isEnabled && canChildScrollUp(this.target) && !isRefreshing
                    && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
            else -> (isEnabled && canChildScrollUp(this.target)
                    && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
        }
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        // Dispatch up to the nested parent
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        totalUnconsumed = 0f
        nestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && totalUnconsumed > 0) {
            if (dy > totalUnconsumed) {
                consumed[1] = dy - totalUnconsumed.toInt()
                totalUnconsumed = 0f
            } else {
                totalUnconsumed -= dy.toFloat()
                consumed[1] = dy

            }
            refreshLog("pre scroll")
            moveSpinner(totalUnconsumed)
        }

        // Now let our nested parent consume the leftovers
        val parentConsumed = parentScrollConsumed
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View) {
        nestedScrollingParentHelper.onStopNestedScroll(target)
        nestedScrollInProgress = false
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (totalUnconsumed > 0) {
            finishSpinner()
            totalUnconsumed = 0f
        }
        // Dispatch up our nested parent
        stopNestedScroll()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                parentOffsetInWindow)

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        val dy = dyUnconsumed + parentOffsetInWindow[1]
        if (dy < 0) {
            totalUnconsumed += Math.abs(dy).toFloat()
            refreshLog("nested scroll")
            moveSpinner(totalUnconsumed)
        }
    }

    // NestedScrollingChild

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        nestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return nestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return nestedScrollingChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return nestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                      dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun onNestedPreFling(target: View, velocityX: Float,
                                  velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float,
                               consumed: Boolean): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            return
        }

        ensureTarget()
        if (target == null) {
            return
        }

        val width = measuredWidth
        val height = measuredHeight
        val targetTop = reviseTargetLayoutTop(paddingTop)
        val targetLeft = paddingLeft
        val targetRight = targetLeft + width - paddingLeft - paddingRight
        val targetBottom = targetTop + height - paddingTop - paddingBottom

        try {
            target?.layout(targetLeft, targetTop, targetRight, targetBottom)
        } catch (ignored: Exception) {
            refreshLog("error: ignored=" + ignored.toString() + " " + ignored.stackTrace.toString())
        }

        val refreshViewLeft = (width - refreshView!!.measuredWidth) / 2
        val refreshViewTop = reviseRefreshViewLayoutTop(refreshInitialOffset.toInt())
        val refreshViewRight = (width + refreshView!!.measuredWidth) / 2
        val refreshViewBottom = refreshViewTop + refreshView!!.measuredHeight

        refreshView?.layout(refreshViewLeft, refreshViewTop, refreshViewRight, refreshViewBottom)

        refreshLog("onLayout: $left : $top : $right : $bottom")
    }

    private fun reviseTargetLayoutTop(layoutTop: Int): Int {
        return when (refreshStyle) {
            RefreshStyle.FLOAT -> layoutTop
            RefreshStyle.PINNED -> layoutTop + targetOrRefreshViewOffsetY.toInt()
            else ->
                //not consider mRefreshResistanceRate < 1.0f
                layoutTop + targetOrRefreshViewOffsetY.toInt()
        }
    }

    private fun reviseRefreshViewLayoutTop(layoutTop: Int): Int {
        return when (refreshStyle) {
            RefreshStyle.FLOAT -> layoutTop + targetOrRefreshViewOffsetY.toInt()
            RefreshStyle.PINNED -> layoutTop
            else ->
                //not consider mRefreshResistanceRate < 1.0f
                layoutTop + targetOrRefreshViewOffsetY.toInt()
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        ensureTarget()
        if (target == null) {
            return
        }

        measureTarget()
        measureRefreshView(widthMeasureSpec, heightMeasureSpec)

        if (!refreshViewMeasured && !usingCustomRefreshInitialOffset) {
            when (refreshStyle) {
                RefreshStyle.PINNED -> {
                    refreshInitialOffset = 0.0f
                    targetOrRefreshViewOffsetY = refreshInitialOffset
                }
                RefreshStyle.FLOAT -> {
                    refreshInitialOffset = (-refreshView!!.measuredHeight).toFloat()
                    targetOrRefreshViewOffsetY = refreshInitialOffset
                }
                else -> {
                    targetOrRefreshViewOffsetY = 0.0f
                    refreshInitialOffset = (-refreshView!!.measuredHeight).toFloat()
                }
            }
        }

        if (!refreshViewMeasured && !usingCustomRefreshTargetOffset) {
            if (refreshTargetOffset < refreshView!!.measuredHeight) {
                refreshTargetOffset = refreshView!!.measuredHeight.toFloat()
            }
        }

        refreshViewMeasured = true

        refreshViewIndex = -1
        for (index in 0 until childCount) {
            if (getChildAt(index) === refreshView) {
                refreshViewIndex = index
                break
            }
        }

    }

    private fun measureTarget() {
        target?.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY))
    }

    private fun measureRefreshView(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = refreshView?.layoutParams as ViewGroup.MarginLayoutParams

        val childWidthMeasureSpec: Int
        childWidthMeasureSpec = if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            val width = Math.max(0, measuredWidth - paddingLeft - paddingRight
                    - lp.leftMargin - lp.rightMargin)
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        } else {
            ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                    paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                    lp.width)
        }

        val childHeightMeasureSpec: Int
        childHeightMeasureSpec = if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            val height = Math.max(0, measuredHeight
                    - paddingTop - paddingBottom
                    - lp.topMargin - lp.bottomMargin)
            View.MeasureSpec.makeMeasureSpec(
                    height, View.MeasureSpec.EXACTLY)
        } else {
            ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                    paddingTop + paddingBottom +
                            lp.topMargin + lp.bottomMargin,
                    lp.height)
        }

        refreshView?.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)

        when (action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                // support compile sdk version < 23
                onStopNestedScroll(this)
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()
        if (target == null) {
            return false
        }

        when (refreshStyle) {
            RefreshStyle.FLOAT -> if (!isEnabled || canChildScrollUp(target)
                    || isRefreshing || nestedScrollInProgress) {
                // Fail fast if we're not in a state where a swipe is possible
                return false
            }
            else -> if (!isEnabled || canChildScrollUp(target) && !dispatchTargetTouchDown) {
                return false
            }
        }

        val action = MotionEventCompat.getActionMasked(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = MotionEventCompat.getPointerId(ev, 0)
                isBeingDragged = false

                val initialDownY = getMotionEventY(ev, activePointerId)
                if (initialDownY == -1f) {
                    return false
                }

                // Animation.AnimationListener.onAnimationEnd() can't be ensured to be called
                if (animateToRefreshingAnimation.hasEnded() && animateToStartAnimation.hasEnded()) {
                    isAnimatingToStart = false
                }

                this.initialDownY = initialDownY
                initialScrollY = targetOrRefreshViewOffsetY
                dispatchTargetTouchDown = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (activePointerId == INVALID_POINTER) {
                    return false
                }

                val activeMoveY = getMotionEventY(ev, activePointerId)
                if (activeMoveY == -1f) {
                    return false
                }

                initDragStatus(activeMoveY)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isBeingDragged = false
                activePointerId = INVALID_POINTER
            }
            else -> {
            }
        }

        return isBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()
        if (target == null) {
            return false
        }

        when (refreshStyle) {
            RefreshStyle.FLOAT -> if (!isEnabled || canChildScrollUp(target) || nestedScrollInProgress) {
                // Fail fast if we're not in a state where a swipe is possible
                return false
            }
            else -> if (!isEnabled || canChildScrollUp(target) && !dispatchTargetTouchDown) {
                return false
            }
        }

        if (refreshStyle == RefreshStyle.FLOAT && (canChildScrollUp(target) || nestedScrollInProgress)) {
            return false
        }

        val action = ev.action

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = MotionEventCompat.getPointerId(ev, 0)
                isBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (activePointerId == INVALID_POINTER) {
                    return false
                }

                val activeMoveY = getMotionEventY(ev, activePointerId)
                if (activeMoveY == -1f) {
                    return false
                }

                val overScrollY: Float
                if (isAnimatingToStart) {
                    overScrollY = targetOrRefreshViewTop.toFloat()

                    initialMotionY = activeMoveY
                    initialScrollY = overScrollY

                    refreshLog("animatetostart overscrollY $overScrollY -- $initialMotionY")
                } else {
                    overScrollY = activeMoveY - initialMotionY + initialScrollY
                    refreshLog("overscrollY $overScrollY -- $initialMotionY -- $initialScrollY")
                }

                if (isRefreshing) {
                    //note: float style will not come here
                    if (overScrollY <= 0) {
                        if (dispatchTargetTouchDown) {
                            target?.dispatchTouchEvent(ev)
                        } else {
                            val obtain = MotionEvent.obtain(ev)
                            obtain.action = MotionEvent.ACTION_DOWN
                            dispatchTargetTouchDown = true
                            target?.dispatchTouchEvent(obtain)
                        }
                    } else if (overScrollY > 0 && overScrollY < refreshTargetOffset) {
                        if (dispatchTargetTouchDown) {
                            val obtain = MotionEvent.obtain(ev)
                            obtain.action = MotionEvent.ACTION_CANCEL
                            dispatchTargetTouchDown = false
                            target?.dispatchTouchEvent(obtain)
                        }
                    }
                    refreshLog("moveSpinner refreshing -- " + initialScrollY + " -- " + (activeMoveY - initialMotionY))
                    moveSpinner(overScrollY)
                } else {
                    if (isBeingDragged) {
                        if (overScrollY > 0) {
                            moveSpinner(overScrollY)
                        } else {
                            return false
                        }
                    } else {
                        initDragStatus(activeMoveY)
                    }
                }
            }

            MotionEventCompat.ACTION_POINTER_DOWN -> {
                onNewerPointerDown(ev)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (activePointerId == INVALID_POINTER || getMotionEventY(ev, activePointerId) == -1f) {
                    resetTouchEvent()
                    return false
                }

                if (isRefreshing || isAnimatingToStart) {
                    if (dispatchTargetTouchDown) {
                        target?.dispatchTouchEvent(ev)
                    }
                    resetTouchEvent()
                    return false
                }

                resetTouchEvent()
                finishSpinner()
                return false
            }
            else -> {
            }
        }

        return true
    }

    private fun resetTouchEvent() {
        initialScrollY = 0.0f

        isBeingDragged = false
        dispatchTargetTouchDown = false
        activePointerId = INVALID_POINTER
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    fun setRefreshing(refreshing: Boolean) {
        if (refreshing && !isRefreshing) {
            isRefreshing = true
            notifyListener = false

            animateToRefreshingPosition(targetOrRefreshViewOffsetY.toInt(), refreshingListener)
        } else {
            setRefreshing(refreshing, false)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (isRefreshing != refreshing) {
            notifyListener = notify
            isRefreshing = refreshing
            if (refreshing) {
                animateToRefreshingPosition(targetOrRefreshViewOffsetY.toInt(), refreshingListener)
            } else {
                animateOffsetToStartPosition(targetOrRefreshViewOffsetY.toInt(), resetListener)
            }
        }
    }

    private fun initDragStatus(activeMoveY: Float) {
        val diff = activeMoveY - initialDownY
        if (isRefreshing && (diff > touchSlop || targetOrRefreshViewOffsetY > 0)) {
            isBeingDragged = true
            initialMotionY = initialDownY + touchSlop
            //scroll direction: from up to down
        } else if (!isBeingDragged && diff > touchSlop) {
            initialMotionY = initialDownY + touchSlop
            isBeingDragged = true
        }
    }

    private fun animateOffsetToStartPosition(from: Int, listener: Animation.AnimationListener) {
        clearAnimation()

        if (computeAnimateToStartDuration(from.toFloat()) <= 0) {
            return
        }

        this.from = from
        animateToStartAnimation.reset()
        animateToStartAnimation.duration = computeAnimateToStartDuration(from.toFloat()).toLong()
        animateToStartAnimation.interpolator = animateToStartInterpolator
        animateToStartAnimation.setAnimationListener(listener)


        startAnimation(animateToStartAnimation)
    }

    private fun animateToRefreshingPosition(from: Int, listener: Animation.AnimationListener) {
        clearAnimation()

        if (computeAnimateToRefreshingDuration(from.toFloat()) <= 0) {
            return
        }

        this.from = from
        animateToRefreshingAnimation.reset()
        animateToRefreshingAnimation.duration = computeAnimateToRefreshingDuration(from.toFloat()).toLong()
        animateToRefreshingAnimation.interpolator = animateToRefreshInterpolator

        animateToRefreshingAnimation.setAnimationListener(listener)

        startAnimation(animateToRefreshingAnimation)
    }

    private fun computeAnimateToRefreshingDuration(from: Float): Int {
        refreshLog("from -- refreshing $from")

        if (from < refreshInitialOffset) {
            return 0
        }

        return when (refreshStyle) {
            RefreshStyle.FLOAT -> (Math.max(0.0f, Math.min(1.0f, Math.abs(from - refreshInitialOffset - refreshTargetOffset) / refreshTargetOffset)) * animateToRefreshDuration).toInt()
            else -> (Math.max(0.0f, Math.min(1.0f, Math.abs(from - refreshTargetOffset) / refreshTargetOffset)) * animateToRefreshDuration).toInt()
        }
    }

    private fun computeAnimateToStartDuration(from: Float): Int {
        refreshLog("from -- start $from")

        if (from < refreshInitialOffset) {
            return 0
        }

        return when (refreshStyle) {
            RefreshStyle.FLOAT -> (Math.max(0.0f, Math.min(1.0f, Math.abs(from - refreshInitialOffset) / refreshTargetOffset)) * animateToStartDuration).toInt()
            else -> (Math.max(0.0f, Math.min(1.0f, Math.abs(from) / refreshTargetOffset)) * animateToStartDuration).toInt()
        }
    }

    /**
     * @param targetOrRefreshViewOffsetY the top position of the target
     * or the RefreshView relative to its parent.
     */
    private fun moveSpinner(targetOrRefreshViewOffsetY: Float) {
        currentTouchOffsetY = targetOrRefreshViewOffsetY

        var convertScrollOffset: Float
        val refreshTargetOffset: Float
        if (!isRefreshing) {
            when (refreshStyle) {
                RefreshStyle.FLOAT -> {
                    convertScrollOffset = refreshInitialOffset + dragDistanceConverter!!.convert(targetOrRefreshViewOffsetY, this.refreshTargetOffset)
                    refreshTargetOffset = this.refreshTargetOffset
                }
                else -> {
                    convertScrollOffset = dragDistanceConverter!!.convert(targetOrRefreshViewOffsetY, this.refreshTargetOffset)
                    refreshTargetOffset = this.refreshTargetOffset
                }
            }
        } else {
            //The Float style will never come here
            convertScrollOffset = if (targetOrRefreshViewOffsetY > this.refreshTargetOffset) {
                this.refreshTargetOffset
            } else {
                targetOrRefreshViewOffsetY
            }

            if (convertScrollOffset < 0.0f) {
                convertScrollOffset = 0.0f
            }

            refreshTargetOffset = this.refreshTargetOffset
        }

        if (!isRefreshing) {
            if (convertScrollOffset > refreshTargetOffset && !isFitRefresh) {
                isFitRefresh = true
                refreshStatus?.pullToRefresh()
            } else if (convertScrollOffset <= refreshTargetOffset && isFitRefresh) {
                isFitRefresh = false
                refreshStatus?.releaseToRefresh()
            }
        }

        refreshLog(targetOrRefreshViewOffsetY.toString() + " -- " + refreshTargetOffset + " -- "
                + convertScrollOffset + " -- " + targetOrRefreshViewOffsetY + " -- " + refreshTargetOffset)

        setTargetOrRefreshViewOffsetY((convertScrollOffset - this.targetOrRefreshViewOffsetY).toInt())
    }

    private fun finishSpinner() {
        if (isRefreshing || isAnimatingToStart) {
            return
        }

        val scrollY = targetOrRefreshViewOffset.toFloat()
        if (scrollY > refreshTargetOffset) {
            setRefreshing(true, true)
        } else {
            isRefreshing = false
            animateOffsetToStartPosition(targetOrRefreshViewOffsetY.toInt(), resetListener)
        }
    }

    private fun onNewerPointerDown(ev: MotionEvent) {
        val index = MotionEventCompat.getActionIndex(ev)
        activePointerId = MotionEventCompat.getPointerId(ev, index)

        initialMotionY = getMotionEventY(ev, activePointerId) - currentTouchOffsetY

        refreshLog(" onDown $initialMotionY")
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)

        if (pointerId == activePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }

        initialMotionY = getMotionEventY(ev, activePointerId) - currentTouchOffsetY

        refreshLog(" onUp $initialMotionY")
    }

    private fun setTargetOrRefreshViewOffsetY(offsetY: Int) {
        if (target == null) {
            return
        }

        targetOrRefreshViewOffsetY = when (refreshStyle) {
            RefreshStyle.FLOAT -> {
                refreshView?.offsetTopAndBottom(offsetY)
                refreshView!!.top.toFloat()
            }
            RefreshStyle.PINNED -> {
                target?.offsetTopAndBottom(offsetY)
                target!!.top.toFloat()
            }
            else -> {
                target?.offsetTopAndBottom(offsetY)
                refreshView?.offsetTopAndBottom(offsetY)
                target!!.top.toFloat()
            }
        }

        refreshLog("current offset $targetOrRefreshViewOffsetY")

        when (refreshStyle) {
            RefreshStyle.FLOAT -> refreshStatus?.pullProgress(targetOrRefreshViewOffsetY,
                    (targetOrRefreshViewOffsetY - refreshInitialOffset) / refreshTargetOffset)
            else -> refreshStatus?.pullProgress(targetOrRefreshViewOffsetY, targetOrRefreshViewOffsetY / refreshTargetOffset)
        }

        if (refreshView?.visibility != View.VISIBLE) {
            refreshView?.visibility = View.VISIBLE
        }

        invalidate()
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(ev, activePointerId)
        return if (index < 0) {
            -1f
        } else MotionEventCompat.getY(ev, index)
    }

    private fun canChildScrollUp(mTarget: View?): Boolean {
        if (mTarget == null) {
            return false
        }

        if (mTarget is ViewGroup) {
            val childCount = mTarget.childCount
            for (i in 0 until childCount) {
                val child = mTarget.getChildAt(i)
                if (canChildScrollUp(child)) {
                    return true
                }
            }
        }

        return ViewCompat.canScrollVertically(mTarget, -1)
    }

    private fun ensureTarget() {
        if (!isTargetValid) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != refreshView) {
                    target = child
                    break
                }
            }
        }
    }

    /**
     * Set the style of the RefreshView.
     *
     * @param refreshStyle One of [RefreshStyle.NORMAL]
     * , [RefreshStyle.PINNED], or [RefreshStyle.FLOAT]
     */
    fun setRefreshStyle(@NonNull refreshStyle: RefreshStyle) {
        this.refreshStyle = refreshStyle
    }

    enum class RefreshStyle {
        NORMAL,
        PINNED,
        FLOAT
    }

    /**
     * Per-child layout information for layouts that support margins.
     */
    class LayoutParams : ViewGroup.MarginLayoutParams {

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {}

        constructor(width: Int, height: Int) : super(width, height) {}

        constructor(source: ViewGroup.MarginLayoutParams) : super(source) {}

        constructor(source: ViewGroup.LayoutParams) : super(source) {}
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    private fun refreshLog(message: String) {
        if (BuildConfig.DEBUG) {
            //Log.i(RefreshLayout::class.java.simpleName, message)
        }
    }
}
