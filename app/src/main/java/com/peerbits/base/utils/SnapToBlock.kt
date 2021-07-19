package com.nlgic.insurance.utils

import android.annotation.TargetApi
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import androidx.recyclerview.widget.SnapHelper
import timber.log.Timber

/*  The number of items in the RecyclerView should be a multiple of block size; otherwise, the
   extra item views will not be positioned on a block boundary when the end of the data is reached.
   Pad out with empty item views if needed.

   Updated to accommodate RTL layouts.
*/   class SnapToBlock(  // Maxim blocks to move during most vigorous fling.
    private val mMaxFlingBlocks: Int
) : SnapHelper() {
    private var mRecyclerView: RecyclerView? = null

    // Total number of items in a block of view in the RecyclerView
    private var mBlocksize = 0

    // Maximum number of positions to move on a fling.
    private var mMaxPositionsToMove = 0

    // Width of a RecyclerView item if orientation is horizonal; height of the item if vertical
    private var mItemDimension = 0

    // Callback interface when blocks are snapped.
    private var mSnapBlockCallback: SnapBlockCallback? = null

    // When snapping, used to determine direction of snap.
    private var mPriorFirstPosition = RecyclerView.NO_POSITION

    // Our private scroller
    private var mScroller: Scroller? = null

    // Horizontal/vertical layout helper
    private var mOrientationHelper: OrientationHelper? = null

    // LTR/RTL helper
    private var mLayoutDirectionHelper: LayoutDirectionHelper? = null
    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView != null) {
            mRecyclerView = recyclerView
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
            if (layoutManager!!.canScrollHorizontally()) {
                mOrientationHelper = OrientationHelper.createHorizontalHelper(layoutManager)
                mLayoutDirectionHelper = LayoutDirectionHelper(
                    ViewCompat.getLayoutDirection(
                        mRecyclerView!!
                    )
                )
            } else if (layoutManager.canScrollVertically()) {
                mOrientationHelper = OrientationHelper.createVerticalHelper(layoutManager)
                // RTL doesn't matter for vertical scrolling for this class.
                mLayoutDirectionHelper = LayoutDirectionHelper(RecyclerView.LAYOUT_DIRECTION_LTR)
            } else {
                throw IllegalStateException("RecyclerView must be scrollable")
            }
            mScroller = Scroller(mRecyclerView!!.context, sInterpolator)
            initItemDimensionIfNeeded(layoutManager)
        }
        super.attachToRecyclerView(recyclerView)
    }

    // Called when the target view is available and we need to know how much more
    // to scroll to get it lined up with the side of the RecyclerView.
    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = mLayoutDirectionHelper!!.getScrollToAlignView(targetView)
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = mLayoutDirectionHelper!!.getScrollToAlignView(targetView)
        }
        if (mSnapBlockCallback != null) {
            if (out[0] == 0 && out[1] == 0) {
                mSnapBlockCallback!!.onBlockSnapped(layoutManager.getPosition(targetView))
            } else {
                mSnapBlockCallback!!.onBlockSnap(layoutManager.getPosition(targetView))
            }
        }
        return out
    }

    // We are flinging and need to know where we are heading.
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int, velocityY: Int
    ): Int {
        val lm = layoutManager as LinearLayoutManager
        initItemDimensionIfNeeded(layoutManager)
        mScroller!!.fling(
            0,
            0,
            velocityX,
            velocityY,
            Int.MIN_VALUE,
            Int.MAX_VALUE,
            Int.MIN_VALUE,
            Int.MAX_VALUE
        )
        if (velocityX != 0) {
            return mLayoutDirectionHelper!!.getPositionsToMove(lm, mScroller!!.finalX, mItemDimension)
        }
        return if (velocityY != 0) {
            mLayoutDirectionHelper!!.getPositionsToMove(lm, mScroller!!.finalY, mItemDimension)
        } else RecyclerView.NO_POSITION
    }

    // We have scrolled to the neighborhood where we will snap. Determine the snap position.
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        // Snap to a view that is either 1) toward the bottom of the data and therefore on screen,
        // or, 2) toward the top of the data and may be off-screen.
        val snapPos = calcTargetPosition(layoutManager as LinearLayoutManager)
        val snapView =
            if (snapPos == RecyclerView.NO_POSITION) null else layoutManager.findViewByPosition(
                snapPos
            )
        if (snapView == null) {
            Timber.d(TAG, "<<<<findSnapView is returning null!")
        }
        Timber.d(TAG, "<<<<findSnapView snapos=$snapPos")
        return snapView
    }

    // Does the heavy lifting for findSnapView.
    private fun calcTargetPosition(layoutManager: LinearLayoutManager): Int {
        val snapPos: Int
        val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
        if (firstVisiblePos == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        initItemDimensionIfNeeded(layoutManager)
        if (firstVisiblePos >= mPriorFirstPosition) {
            // Scrolling toward bottom of data
            val firstCompletePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            snapPos = if (firstCompletePosition != RecyclerView.NO_POSITION
                && firstCompletePosition % mBlocksize == 0
            ) {
                firstCompletePosition
            } else {
                roundDownToBlockSize(firstVisiblePos + mBlocksize)
            }
        } else {
            // Scrolling toward top of data
            snapPos = roundDownToBlockSize(firstVisiblePos)
            // Check to see if target view exists. If it doesn't, force a smooth scroll.
            // SnapHelper only snaps to existing views and will not scroll to a non-existant one.
            // If limiting fling to single block, then the following is not needed since the
            // views are likely to be in the RecyclerView pool.
            if (layoutManager.findViewByPosition(snapPos) == null) {
                val toScroll =
                    mLayoutDirectionHelper!!.calculateDistanceToScroll(layoutManager, snapPos)
                mRecyclerView!!.smoothScrollBy(toScroll[0], toScroll[1], sInterpolator)
            }
        }
        mPriorFirstPosition = firstVisiblePos
        return snapPos
    }

    private fun initItemDimensionIfNeeded(layoutManager: RecyclerView.LayoutManager?) {
        if (mItemDimension != 0) {
            return
        }
        var child: View?
        child = layoutManager?.getChildAt(0)
        if (child == null) {
            return;
        }
        if (layoutManager?.canScrollHorizontally()!!) {
            mItemDimension = child.width
            mBlocksize = getSpanCount(layoutManager) * (mRecyclerView!!.width / mItemDimension)
        } else if (layoutManager.canScrollVertically()) {
            mItemDimension = child.height
            mBlocksize = getSpanCount(layoutManager) * (mRecyclerView!!.height / mItemDimension)
        }
        mMaxPositionsToMove = mBlocksize * mMaxFlingBlocks
    }

    private fun getSpanCount(layoutManager: RecyclerView.LayoutManager?): Int {
        return if (layoutManager is GridLayoutManager) layoutManager.spanCount else 1
    }

    private fun roundDownToBlockSize(trialPosition: Int): Int {
        return trialPosition - trialPosition % mBlocksize
    }

    private fun roundUpToBlockSize(trialPosition: Int): Int {
        return roundDownToBlockSize(trialPosition + mBlocksize - 1)
    }

    override fun createScroller(layoutManager: RecyclerView.LayoutManager): LinearSmoothScroller? {
        return if (layoutManager !is ScrollVectorProvider) {
            null
        } else object : LinearSmoothScroller(mRecyclerView!!.context) {
            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                val snapDistances = calculateDistanceToFinalSnap(
                    mRecyclerView!!.layoutManager!!,
                    targetView
                )
                val dx = snapDistances[0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, sInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
    }

    fun setSnapBlockCallback(callback: SnapBlockCallback?) {
        mSnapBlockCallback = callback
    }

    /*
        Helper class that handles calculations for LTR and RTL layouts.
     */
    private inner class LayoutDirectionHelper @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) internal constructor(
        direction: Int
    ) {
        // Is the layout an RTL one?
        private val mIsRTL: Boolean

        /*
            Calculate the amount of scroll needed to align the target view with the layout edge.
         */
        fun getScrollToAlignView(targetView: View?): Int {
            return if (mIsRTL) mOrientationHelper!!.getDecoratedEnd(targetView) - mRecyclerView!!.width else mOrientationHelper!!.getDecoratedStart(
                targetView
            )
        }

        /**
         * Calculate the distance to final snap position when the view corresponding to the snap
         * position is not currently available.
         *
         * @param layoutManager LinearLayoutManager or descendent class
         * @param targetPos     - Adapter position to snap to
         * @return int[2] {x-distance in pixels, y-distance in pixels}
         */
        fun calculateDistanceToScroll(
            layoutManager: LinearLayoutManager,
            targetPos: Int
        ): IntArray {
            val out = IntArray(2)
            val firstVisiblePos: Int
            firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
            if (layoutManager.canScrollHorizontally()) {
                if (targetPos <= firstVisiblePos) { // scrolling toward top of data
                    if (mIsRTL) {
                        val lastView =
                            layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition())
                        out[0] = (mOrientationHelper!!.getDecoratedEnd(lastView)
                            + (firstVisiblePos - targetPos) * mItemDimension)
                    } else {
                        val firstView = layoutManager.findViewByPosition(firstVisiblePos)
                        out[0] = (mOrientationHelper!!.getDecoratedStart(firstView)
                            - (firstVisiblePos - targetPos) * mItemDimension)
                    }
                }
            }
            if (layoutManager.canScrollVertically()) {
                if (targetPos <= firstVisiblePos) { // scrolling toward top of data
                    val firstView = layoutManager.findViewByPosition(firstVisiblePos)
                    out[1] = firstView!!.top - (firstVisiblePos - targetPos) * mItemDimension
                }
            }
            return out
        }

        /*
            Calculate the number of positions to move in the RecyclerView given a scroll amount
            and the size of the items to be scrolled. Return integral multiple of mBlockSize not
            equal to zero.
         */
        fun getPositionsToMove(llm: LinearLayoutManager, scroll: Int, itemSize: Int): Int {
            var positionsToMove: Int
            positionsToMove = roundUpToBlockSize(Math.abs(scroll) / itemSize)
            if (positionsToMove < mBlocksize) {
                // Must move at least one block
                positionsToMove = mBlocksize
            } else if (positionsToMove > mMaxPositionsToMove) {
                // Clamp number of positions to move so we don't get wild flinging.
                positionsToMove = mMaxPositionsToMove
            }
            if (scroll < 0) {
                positionsToMove *= -1
            }
            if (mIsRTL) {
                positionsToMove *= -1
            }
            return if (mLayoutDirectionHelper!!.isDirectionToBottom(scroll < 0)) {
                // Scrolling toward the bottom of data.
                roundDownToBlockSize(llm.findFirstVisibleItemPosition()) + positionsToMove
            } else roundDownToBlockSize(llm.findLastVisibleItemPosition()) + positionsToMove
            // Scrolling toward the top of the data.
        }

        fun isDirectionToBottom(velocityNegative: Boolean): Boolean {
            return if (mIsRTL) velocityNegative else !velocityNegative
        }

        init {
            mIsRTL = direction == View.LAYOUT_DIRECTION_RTL
        }
    }

    interface SnapBlockCallback {
        fun onBlockSnap(snapPosition: Int)
        fun onBlockSnapped(snapPosition: Int)
    }

    companion object {
        // Borrowed from ViewPager.java
        private val sInterpolator =
            Interpolator { t -> // _o(t) = t * t * ((tension + 1) * t + tension)
                // o(t) = _o(t - 1) + 1
                var t = t
                t -= 1.0f
                t * t * t + 1.0f
            }
        private const val MILLISECONDS_PER_INCH = 100f
        private const val TAG = "SnapToBlock"
    }
}