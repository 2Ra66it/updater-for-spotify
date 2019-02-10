package ru.ra66it.updaterforspotify.presentation.ui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

public class RefreshLayout extends ViewGroup
        implements NestedScrollingParent, NestedScrollingChild {

    private static final int INVALID_INDEX = -1;
    private static final int INVALID_POINTER = -1;
    //the default height of the RefreshView
    private static final int DEFAULT_REFRESH_SIZE_DP = 30;
    //the animation duration of the RefreshView scroll to the refresh point or the start point
    private static final int DEFAULT_ANIMATE_DURATION = 300;
    // the threshold of the trigger to refresh
    private static final int DEFAULT_REFRESH_TARGET_OFFSET_DP = 40;

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0f;

    // NestedScroll
    private float totalUnconsumed;
    private boolean nestedScrollInProgress;
    private final int[] parentScrollConsumed = new int[2];
    private final int[] parentOffsetInWindow = new int[2];
    private final NestedScrollingChildHelper nestedScrollingChildHelper;
    private final NestedScrollingParentHelper nestedScrollingParentHelper;

    //whether to remind the callback listener(OnRefreshListener)
    private boolean isAnimatingToStart;
    private boolean isRefreshing;
    private boolean isFitRefresh;
    private boolean isBeingDragged;
    private boolean notifyListener;
    private boolean dispatchTargetTouchDown;

    private int refreshViewIndex = INVALID_INDEX;
    private int activePointerId = INVALID_POINTER;
    private int animateToStartDuration = DEFAULT_ANIMATE_DURATION;
    private int animateToRefreshDuration = DEFAULT_ANIMATE_DURATION;

    private int from;
    private int touchSlop;
    private int refreshViewSize;

    private float initialDownY;
    private float initialScrollY;
    private float initialMotionY;
    private float currentTouchOffsetY;
    private float targetOrRefreshViewOffsetY;
    private float refreshInitialOffset;
    private float refreshTargetOffset;

    // Whether the client has set a custom refreshing position;
    private boolean usingCustomRefreshTargetOffset = false;
    // Whether the client has set a custom starting position;
    private boolean usingCustomRefreshInitialOffset = false;
    // Whether or not the RefreshView has been measured.
    private boolean refreshViewMeasured = false;

    private RefreshStyle refreshStyle = RefreshStyle.NORMAL;

    private View target;
    private View refreshView;

    private IDragDistanceConverter dragDistanceConverter;

    private IRefreshStatus refreshStatus;
    private OnRefreshListener onRefreshListener;

    private Interpolator animateToStartInterpolator
            = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
    private Interpolator animateToRefreshInterpolator
            = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

    private final Animation animateToRefreshingAnimation = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            switch (refreshStyle) {
                case FLOAT:
                    float refreshTargetOffset = RefreshLayout.this.refreshTargetOffset + refreshInitialOffset;
                    animateToTargetOffset(refreshTargetOffset, refreshView.getTop(), interpolatedTime);
                    break;
                default:
                    animateToTargetOffset(RefreshLayout.this.refreshTargetOffset, target.getTop(), interpolatedTime);
                    break;
            }
        }
    };

    private final Animation animateToStartAnimation = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            switch (refreshStyle) {
                case FLOAT:
                    animateToTargetOffset(refreshInitialOffset, refreshView.getTop(), interpolatedTime);
                    break;
                default:
                    animateToTargetOffset(0.0f, target.getTop(), interpolatedTime);
                    break;
            }
        }
    };

    private void animateToTargetOffset(float targetEnd, float currentOffset, float interpolatedTime) {
        int targetOffset = (int) (from + (targetEnd - from) * interpolatedTime);

        setTargetOrRefreshViewOffsetY((int) (targetOffset - currentOffset));
    }

    private final Animation.AnimationListener refreshingListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimatingToStart = true;
            refreshStatus.refreshing();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (notifyListener) {
                if (onRefreshListener != null) {
                    onRefreshListener.onRefresh();
                }
            }

            isAnimatingToStart = false;
        }
    };

    private final Animation.AnimationListener resetListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimatingToStart = true;
            refreshStatus.refreshComplete();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            reset();
        }
    };

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        refreshViewSize = (int) (DEFAULT_REFRESH_SIZE_DP * metrics.density);

        refreshTargetOffset = DEFAULT_REFRESH_TARGET_OFFSET_DP * metrics.density;

        targetOrRefreshViewOffsetY = 0.0f;
        refreshInitialOffset = 0.0f;

        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        initRefreshView();
        initDragDistanceConverter();
        setNestedScrollingEnabled(true);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    @Override
    protected void onDetachedFromWindow() {
        reset();
        clearAnimation();
        super.onDetachedFromWindow();
    }

    private void reset() {
        setTargetOrRefreshViewToInitial();

        currentTouchOffsetY = 0.0f;

        refreshStatus.reset();
        refreshView.setVisibility(View.GONE);

        isRefreshing = false;
        isAnimatingToStart = false;
    }

    private void setTargetOrRefreshViewToInitial() {
        switch (refreshStyle) {
            case FLOAT:
                setTargetOrRefreshViewOffsetY((int) (refreshInitialOffset - targetOrRefreshViewOffsetY));
                break;
            default:
                setTargetOrRefreshViewOffsetY((int) (0 - targetOrRefreshViewOffsetY));
                break;
        }
    }

    private void initRefreshView() {
        refreshView = new RefreshView(getContext());
        refreshView.setVisibility(View.GONE);
        if (refreshView instanceof IRefreshStatus) {
            refreshStatus = (IRefreshStatus) refreshView;
        } else {
            throw new ClassCastException("the refreshView must implement the interface IRefreshStatus");
        }

        LayoutParams layoutParams = new LayoutParams(refreshViewSize, refreshViewSize);
        addView(refreshView, layoutParams);
    }

    private void initDragDistanceConverter() {
        dragDistanceConverter = new MaterialDragDistanceConverter();
    }

    /**
     * @param refreshView  must implements the interface IRefreshStatus
     * @param layoutParams the with is always the match_parent， no matter how you set
     *                     the height you need to set a specific value
     */
    public void setRefreshView(@NonNull View refreshView, ViewGroup.LayoutParams layoutParams) {
        if (refreshView == null) {
            throw new NullPointerException("the refreshView can't be null");
        }

        if (this.refreshView == refreshView) {
            return;
        }

        if (this.refreshView != null && this.refreshView.getParent() != null) {
            ((ViewGroup) this.refreshView.getParent()).removeView(this.refreshView);
        }

        if (refreshView instanceof IRefreshStatus) {
            refreshStatus = (IRefreshStatus) refreshView;
        } else {
            throw new ClassCastException("the refreshView must implement the interface IRefreshStatus");
        }
        refreshView.setVisibility(View.GONE);
        addView(refreshView, layoutParams);

        this.refreshView = refreshView;
    }

    public void setDragDistanceConverter(@NonNull IDragDistanceConverter dragDistanceConverter) {
        if (dragDistanceConverter == null) {
            throw new NullPointerException("the dragDistanceConverter can't be null");
        }
        this.dragDistanceConverter = dragDistanceConverter;
    }

    /**
     * @param animateToStartInterpolator The interpolator used by the animation that
     *                                   move the refresh view from the refreshing point or
     *                                   (the release point) to the start point.
     */
    public void setAnimateToStartInterpolator(@NonNull Interpolator animateToStartInterpolator) {
        if (animateToStartInterpolator == null) {
            throw new NullPointerException("the animateToStartInterpolator can't be null");
        }

        this.animateToStartInterpolator = animateToStartInterpolator;
    }

    /**
     * @param animateToRefreshInterpolator The interpolator used by the animation that
     *                                     move the refresh view the release point to the refreshing point.
     */
    public void setAnimateToRefreshInterpolator(@NonNull Interpolator animateToRefreshInterpolator) {
        if (animateToRefreshInterpolator == null) {
            throw new NullPointerException("the animateToRefreshInterpolator can't be null");
        }

        this.animateToRefreshInterpolator = animateToRefreshInterpolator;
    }

    /**
     * @param animateToStartDuration The duration used by the animation that
     *                               move the refresh view from the refreshing point or
     *                               (the release point) to the start point.
     */
    public void setAnimateToStartDuration(int animateToStartDuration) {
        this.animateToStartDuration = animateToStartDuration;
    }

    /**
     * @param animateToRefreshDuration The duration used by the animation that
     *                                 move the refresh view the release point to the refreshing point.
     */
    public void setAnimateToRefreshDuration(int animateToRefreshDuration) {
        this.animateToRefreshDuration = animateToRefreshDuration;
    }

    /**
     * @param refreshTargetOffset The minimum distance that trigger refresh.
     */
    public void setRefreshTargetOffset(float refreshTargetOffset) {
        this.refreshTargetOffset = refreshTargetOffset;
        usingCustomRefreshTargetOffset = true;
        requestLayout();
    }

    /**
     * @param refreshInitialOffset the top position of the {@link #refreshView} relative to its parent.
     */
    public void setRefreshInitialOffset(float refreshInitialOffset) {
        this.refreshInitialOffset = refreshInitialOffset;
        usingCustomRefreshInitialOffset = true;
        requestLayout();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        switch (refreshStyle) {
            case FLOAT:
                if (refreshViewIndex < 0) {
                    return i;
                } else if (i == childCount - 1) {
                    // Draw the selected child last
                    return refreshViewIndex;
                } else if (i >= refreshViewIndex) {
                    // Move the children after the selected child earlier one
                    return i + 1;
                } else {
                    // Keep the children before the selected child the same
                    return i;
                }
            default:
                if (refreshViewIndex < 0) {
                    return i;
                } else if (i == 0) {
                    // Draw the selected child first
                    return refreshViewIndex;
                } else if (i <= refreshViewIndex) {
                    // Move the children before the selected child earlier one
                    return i - 1;
                } else {
                    return i;
                }
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && target instanceof AbsListView)
                || (target != null && !ViewCompat.isNestedScrollingEnabled(target))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // NestedScrollingParent
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        switch (refreshStyle) {
            case FLOAT:
                return isEnabled() && canChildScrollUp(this.target) && !isRefreshing
                        && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
            default:
                return isEnabled() && canChildScrollUp(this.target)
                        && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        totalUnconsumed = 0;
        nestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && totalUnconsumed > 0) {
            if (dy > totalUnconsumed) {
                consumed[1] = dy - (int) totalUnconsumed;
                totalUnconsumed = 0;
            } else {
                totalUnconsumed -= dy;
                consumed[1] = dy;

            }
            //RefreshLogger.i("pre scroll");
            moveSpinner(totalUnconsumed);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = parentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        nestedScrollingParentHelper.onStopNestedScroll(target);
        nestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (totalUnconsumed > 0) {
            finishSpinner();
            totalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                parentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + parentOffsetInWindow[1];
        if (dy < 0) {
            totalUnconsumed += Math.abs(dy);
           // RefreshLogger.i("nested scroll");
            moveSpinner(totalUnconsumed);
        }
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() == 0) {
            return;
        }

        ensureTarget();
        if (target == null) {
            return;
        }

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int targetTop = reviseTargetLayoutTop(getPaddingTop());
        final int targetLeft = getPaddingLeft();
        final int targetRight = targetLeft + width - getPaddingLeft() - getPaddingRight();
        final int targetBottom = targetTop + height - getPaddingTop() - getPaddingBottom();

        try {
            target.layout(targetLeft, targetTop, targetRight, targetBottom);
        } catch (Exception ignored) {
           // RefreshLogger.e("error: ignored=" + ignored.toString() + " " + ignored.getStackTrace().toString());
        }

        int refreshViewLeft = (width - refreshView.getMeasuredWidth()) / 2;
        int refreshViewTop = reviseRefreshViewLayoutTop((int) refreshInitialOffset);
        int refreshViewRight = (width + refreshView.getMeasuredWidth()) / 2;
        int refreshViewBottom = refreshViewTop + refreshView.getMeasuredHeight();

        refreshView.layout(refreshViewLeft, refreshViewTop, refreshViewRight, refreshViewBottom);

        //RefreshLogger.i("onLayout: " + left + " : " + top + " : " + right + " : " + bottom);
    }

    private int reviseTargetLayoutTop(int layoutTop) {
        switch (refreshStyle) {
            case FLOAT:
                return layoutTop;
            case PINNED:
                return layoutTop + (int) targetOrRefreshViewOffsetY;
            default:
                //not consider mRefreshResistanceRate < 1.0f
                return layoutTop + (int) targetOrRefreshViewOffsetY;
        }
    }

    private int reviseRefreshViewLayoutTop(int layoutTop) {
        switch (refreshStyle) {
            case FLOAT:
                return layoutTop + (int) targetOrRefreshViewOffsetY;
            case PINNED:
                return layoutTop;
            default:
                //not consider mRefreshResistanceRate < 1.0f
                return layoutTop + (int) targetOrRefreshViewOffsetY;
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ensureTarget();
        if (target == null) {
            return;
        }

        measureTarget();
        measureRefreshView(widthMeasureSpec, heightMeasureSpec);

        if (!refreshViewMeasured && !usingCustomRefreshInitialOffset) {
            switch (refreshStyle) {
                case PINNED:
                    targetOrRefreshViewOffsetY = refreshInitialOffset = 0.0f;
                    break;
                case FLOAT:
                    targetOrRefreshViewOffsetY = refreshInitialOffset = -refreshView.getMeasuredHeight();
                    break;
                default:
                    targetOrRefreshViewOffsetY = 0.0f;
                    refreshInitialOffset = -refreshView.getMeasuredHeight();
                    break;
            }
        }

        if (!refreshViewMeasured && !usingCustomRefreshTargetOffset) {
            if (refreshTargetOffset < refreshView.getMeasuredHeight()) {
                refreshTargetOffset = refreshView.getMeasuredHeight();
            }
        }

        refreshViewMeasured = true;

        refreshViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == refreshView) {
                refreshViewIndex = index;
                break;
            }
        }

    }

    private void measureTarget() {
        target.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
    }

    private void measureRefreshView(int widthMeasureSpec, int heightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) refreshView.getLayoutParams();

        final int childWidthMeasureSpec;
        if (lp.width == LayoutParams.MATCH_PARENT) {
            final int width = Math.max(0, getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                    - lp.leftMargin - lp.rightMargin);
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        } else {
            childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
        }

        final int childHeightMeasureSpec;
        if (lp.height == LayoutParams.MATCH_PARENT) {
            final int height = Math.max(0, getMeasuredHeight()
                    - getPaddingTop() - getPaddingBottom()
                    - lp.topMargin - lp.bottomMargin);
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    height, MeasureSpec.EXACTLY);
        } else {
            childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                    getPaddingTop() + getPaddingBottom() +
                            lp.topMargin + lp.bottomMargin,
                    lp.height);
        }

        refreshView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // support compile sdk version < 23
                onStopNestedScroll(this);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (target == null) {
            return false;
        }

        switch (refreshStyle) {
            case FLOAT:
                if (!isEnabled() || canChildScrollUp(target)
                        || isRefreshing || nestedScrollInProgress) {
                    // Fail fast if we're not in a state where a swipe is possible
                    return false;
                }
                break;
            default:
                if ((!isEnabled() || (canChildScrollUp(target) && !dispatchTargetTouchDown))) {
                    return false;
                }
                break;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = MotionEventCompat.getPointerId(ev, 0);
                isBeingDragged = false;

                float initialDownY = getMotionEventY(ev, activePointerId);
                if (initialDownY == -1) {
                    return false;
                }

                // Animation.AnimationListener.onAnimationEnd() can't be ensured to be called
                if (animateToRefreshingAnimation.hasEnded() && animateToStartAnimation.hasEnded()) {
                    isAnimatingToStart = false;
                }

                this.initialDownY = initialDownY;
                initialScrollY = targetOrRefreshViewOffsetY;
                dispatchTargetTouchDown = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (activePointerId == INVALID_POINTER) {
                    return false;
                }

                float activeMoveY = getMotionEventY(ev, activePointerId);
                if (activeMoveY == -1) {
                    return false;
                }

                initDragStatus(activeMoveY);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                activePointerId = INVALID_POINTER;
                break;
            default:
                break;
        }

        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (target == null) {
            return false;
        }

        switch (refreshStyle) {
            case FLOAT:
                if (!isEnabled() || canChildScrollUp(target) || nestedScrollInProgress) {
                    // Fail fast if we're not in a state where a swipe is possible
                    return false;
                }
                break;
            default:
                if ((!isEnabled() || (canChildScrollUp(target) && !dispatchTargetTouchDown))) {
                    return false;
                }
                break;
        }

        if (refreshStyle == RefreshStyle.FLOAT && (canChildScrollUp(target) || nestedScrollInProgress)) {
            return false;
        }

        final int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = MotionEventCompat.getPointerId(ev, 0);
                isBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                if (activePointerId == INVALID_POINTER) {
                    return false;
                }

                final float activeMoveY = getMotionEventY(ev, activePointerId);
                if (activeMoveY == -1) {
                    return false;
                }

                float overScrollY;
                if (isAnimatingToStart) {
                    overScrollY = getTargetOrRefreshViewTop();

                    initialMotionY = activeMoveY;
                    initialScrollY = overScrollY;

                    //RefreshLogger.i("animatetostart overscrolly " + overScrollY + " -- " + initialMotionY);
                } else {
                    overScrollY = activeMoveY - initialMotionY + initialScrollY;
                    //RefreshLogger.i("overscrolly " + overScrollY + " --" + initialMotionY + " -- " + initialScrollY);
                }

                if (isRefreshing) {
                    //note: float style will not come here
                    if (overScrollY <= 0) {
                        if (dispatchTargetTouchDown) {
                            target.dispatchTouchEvent(ev);
                        } else {
                            MotionEvent obtain = MotionEvent.obtain(ev);
                            obtain.setAction(MotionEvent.ACTION_DOWN);
                            dispatchTargetTouchDown = true;
                            target.dispatchTouchEvent(obtain);
                        }
                    } else if (overScrollY > 0 && overScrollY < refreshTargetOffset) {
                        if (dispatchTargetTouchDown) {
                            MotionEvent obtain = MotionEvent.obtain(ev);
                            obtain.setAction(MotionEvent.ACTION_CANCEL);
                            dispatchTargetTouchDown = false;
                            target.dispatchTouchEvent(obtain);
                        }
                    }
                    //RefreshLogger.i("moveSpinner refreshing -- " + initialScrollY + " -- " + (activeMoveY - initialMotionY));
                    moveSpinner(overScrollY);
                } else {
                    if (isBeingDragged) {
                        if (overScrollY > 0) {
                            moveSpinner(overScrollY);
                        } else {
                            return false;
                        }
                    } else {
                        initDragStatus(activeMoveY);
                    }
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                onNewerPointerDown(ev);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (activePointerId == INVALID_POINTER
                        || getMotionEventY(ev, activePointerId) == -1) {
                    resetTouchEvent();
                    return false;
                }

                if (isRefreshing || isAnimatingToStart) {
                    if (dispatchTargetTouchDown) {
                        target.dispatchTouchEvent(ev);
                    }
                    resetTouchEvent();
                    return false;
                }

                resetTouchEvent();
                finishSpinner();
                return false;
            }
            default:
                break;
        }

        return true;
    }

    private void resetTouchEvent() {
        initialScrollY = 0.0f;

        isBeingDragged = false;
        dispatchTargetTouchDown = false;
        activePointerId = INVALID_POINTER;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && !isRefreshing) {
            isRefreshing = true;
            notifyListener = false;

            animateToRefreshingPosition((int) targetOrRefreshViewOffsetY, refreshingListener);
        } else {
            setRefreshing(refreshing, false);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (isRefreshing != refreshing) {
            notifyListener = notify;
            isRefreshing = refreshing;
            if (refreshing) {
                animateToRefreshingPosition((int) targetOrRefreshViewOffsetY, refreshingListener);
            } else {
                animateOffsetToStartPosition((int) targetOrRefreshViewOffsetY, resetListener);
            }
        }
    }

    private void initDragStatus(float activeMoveY) {
        float diff = activeMoveY - initialDownY;
        if (isRefreshing && (diff > touchSlop || targetOrRefreshViewOffsetY > 0)) {
            isBeingDragged = true;
            initialMotionY = initialDownY + touchSlop;
            //scroll direction: from up to down
        } else if (!isBeingDragged && diff > touchSlop) {
            initialMotionY = initialDownY + touchSlop;
            isBeingDragged = true;
        }
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        clearAnimation();

        if (computeAnimateToStartDuration(from) <= 0) {
            listener.onAnimationStart(null);
            listener.onAnimationEnd(null);
            return;
        }

        this.from = from;
        animateToStartAnimation.reset();
        animateToStartAnimation.setDuration(computeAnimateToStartDuration(from));
        animateToStartAnimation.setInterpolator(animateToStartInterpolator);
        if (listener != null) {
            animateToStartAnimation.setAnimationListener(listener);
        }

        startAnimation(animateToStartAnimation);
    }

    private void animateToRefreshingPosition(int from, Animation.AnimationListener listener) {
        clearAnimation();

        if (computeAnimateToRefreshingDuration(from) <= 0) {
            listener.onAnimationStart(null);
            listener.onAnimationEnd(null);
            return;
        }

        this.from = from;
        animateToRefreshingAnimation.reset();
        animateToRefreshingAnimation.setDuration(computeAnimateToRefreshingDuration(from));
        animateToRefreshingAnimation.setInterpolator(animateToRefreshInterpolator);

        if (listener != null) {
            animateToRefreshingAnimation.setAnimationListener(listener);
        }

        startAnimation(animateToRefreshingAnimation);
    }

    private int computeAnimateToRefreshingDuration(float from) {
        //RefreshLogger.i("from -- refreshing " + from);

        if (from < refreshInitialOffset) {
            return 0;
        }

        switch (refreshStyle) {
            case FLOAT:
                return (int) (Math.max(0.0f, Math.min(1.0f, Math.abs(from - refreshInitialOffset - refreshTargetOffset) / refreshTargetOffset))
                        * animateToRefreshDuration);
            default:
                return (int) (Math.max(0.0f, Math.min(1.0f, Math.abs(from - refreshTargetOffset) / refreshTargetOffset))
                        * animateToRefreshDuration);
        }
    }

    private int computeAnimateToStartDuration(float from) {
        //RefreshLogger.i("from -- start " + from);

        if (from < refreshInitialOffset) {
            return 0;
        }

        switch (refreshStyle) {
            case FLOAT:
                return (int) (Math.max(0.0f, Math.min(1.0f, Math.abs(from - refreshInitialOffset) / refreshTargetOffset))
                        * animateToStartDuration);
            default:
                return (int) (Math.max(0.0f, Math.min(1.0f, Math.abs(from) / refreshTargetOffset))
                        * animateToStartDuration);
        }
    }

    /**
     * @param targetOrRefreshViewOffsetY the top position of the target
     *                                   or the RefreshView relative to its parent.
     */
    private void moveSpinner(float targetOrRefreshViewOffsetY) {
        currentTouchOffsetY = targetOrRefreshViewOffsetY;

        float convertScrollOffset;
        float refreshTargetOffset;
        if (!isRefreshing) {
            switch (refreshStyle) {
                case FLOAT:
                    convertScrollOffset = refreshInitialOffset
                            + dragDistanceConverter.convert(targetOrRefreshViewOffsetY, this.refreshTargetOffset);
                    refreshTargetOffset = this.refreshTargetOffset;
                    break;
                default:
                    convertScrollOffset = dragDistanceConverter.convert(targetOrRefreshViewOffsetY, this.refreshTargetOffset);
                    refreshTargetOffset = this.refreshTargetOffset;
                    break;
            }
        } else {
            //The Float style will never come here
            if (targetOrRefreshViewOffsetY > this.refreshTargetOffset) {
                convertScrollOffset = this.refreshTargetOffset;
            } else {
                convertScrollOffset = targetOrRefreshViewOffsetY;
            }

            if (convertScrollOffset < 0.0f) {
                convertScrollOffset = 0.0f;
            }

            refreshTargetOffset = this.refreshTargetOffset;
        }

        if (!isRefreshing) {
            if (convertScrollOffset > refreshTargetOffset && !isFitRefresh) {
                isFitRefresh = true;
                refreshStatus.pullToRefresh();
            } else if (convertScrollOffset <= refreshTargetOffset && isFitRefresh) {
                isFitRefresh = false;
                refreshStatus.releaseToRefresh();
            }
        }

        //RefreshLogger.i(targetOrRefreshViewOffsetY + " -- " + refreshTargetOffset + " -- "
        //        + convertScrollOffset + " -- " + targetOrRefreshViewOffsetY + " -- " + refreshTargetOffset);

        setTargetOrRefreshViewOffsetY((int) (convertScrollOffset - this.targetOrRefreshViewOffsetY));
    }

    private void finishSpinner() {
        if (isRefreshing || isAnimatingToStart) {
            return;
        }

        float scrollY = getTargetOrRefreshViewOffset();
        if (scrollY > refreshTargetOffset) {
            setRefreshing(true, true);
        } else {
            isRefreshing = false;
            animateOffsetToStartPosition((int) targetOrRefreshViewOffsetY, resetListener);
        }
    }

    private void onNewerPointerDown(MotionEvent ev) {
        final int index = MotionEventCompat.getActionIndex(ev);
        activePointerId = MotionEventCompat.getPointerId(ev, index);

        initialMotionY = getMotionEventY(ev, activePointerId) - currentTouchOffsetY;

        //RefreshLogger.i(" onDown " + initialMotionY);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

        if (pointerId == activePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }

        initialMotionY = getMotionEventY(ev, activePointerId) - currentTouchOffsetY;

       // RefreshLogger.i(" onUp " + initialMotionY);
    }

    private void setTargetOrRefreshViewOffsetY(int offsetY) {
        if (target == null) {
            return;
        }

        switch (refreshStyle) {
            case FLOAT:
                refreshView.offsetTopAndBottom(offsetY);
                targetOrRefreshViewOffsetY = refreshView.getTop();
                break;
            case PINNED:
                target.offsetTopAndBottom(offsetY);
                targetOrRefreshViewOffsetY = target.getTop();
                break;
            default:
                target.offsetTopAndBottom(offsetY);
                refreshView.offsetTopAndBottom(offsetY);
                targetOrRefreshViewOffsetY = target.getTop();
                break;
        }

        //RefreshLogger.i("current offset" + targetOrRefreshViewOffsetY);

        switch (refreshStyle) {
            case FLOAT:
                refreshStatus.pullProgress(targetOrRefreshViewOffsetY,
                        (targetOrRefreshViewOffsetY - refreshInitialOffset) / refreshTargetOffset);
                break;
            default:
                refreshStatus.pullProgress(targetOrRefreshViewOffsetY, targetOrRefreshViewOffsetY / refreshTargetOffset);
                break;
        }

        if (refreshView.getVisibility() != View.VISIBLE) {
            refreshView.setVisibility(View.VISIBLE);
        }

        invalidate();
    }

    private int getTargetOrRefreshViewTop() {
        switch (refreshStyle) {
            case FLOAT:
                return refreshView.getTop();
            default:
                return target.getTop();
        }
    }

    private int getTargetOrRefreshViewOffset() {
        switch (refreshStyle) {
            case FLOAT:
                return (int) (refreshView.getTop() - refreshInitialOffset);
            default:
                return target.getTop();
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private boolean canChildScrollUp(View mTarget) {
        if (mTarget == null) {
            return false;
        }

        if (android.os.Build.VERSION.SDK_INT < 14 && mTarget instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mTarget;
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        }

        if (mTarget instanceof ViewGroup) {
            int childCount = ((ViewGroup) mTarget).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = ((ViewGroup) mTarget).getChildAt(i);
                if (canChildScrollUp(child)) {
                    return true;
                }
            }
        }

        return ViewCompat.canScrollVertically(mTarget, -1);
    }

    private void ensureTarget() {
        if (!isTargetValid()) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(refreshView)) {
                    target = child;
                    break;
                }
            }
        }
    }

    private boolean isTargetValid() {
        for (int i = 0; i < getChildCount(); i++) {
            if (target == getChildAt(i)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Set the style of the RefreshView.
     *
     * @param refreshStyle One of {@link RefreshStyle#NORMAL}
     *                     , {@link RefreshStyle#PINNED}, or {@link RefreshStyle#FLOAT}
     */
    public void setRefreshStyle(@NonNull RefreshStyle refreshStyle) {
        this.refreshStyle = refreshStyle;
    }

    public enum RefreshStyle {
        NORMAL,
        PINNED,
        FLOAT
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        onRefreshListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * Per-child layout information for layouts that support margins.
     */
    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }
}
