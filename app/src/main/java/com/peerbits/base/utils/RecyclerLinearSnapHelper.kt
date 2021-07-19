package com.nlgic.insurance.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class RecyclerLinearSnapHelper @JvmOverloads constructor() :
    LinearSnapHelper() {
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {

        val centerView = findSnapView(layoutManager);
        if (centerView == null)
            return RecyclerView.NO_POSITION;

        val position = layoutManager.getPosition(centerView);
        var targetPosition = -1;
        if (layoutManager.canScrollHorizontally()) {
            if (velocityX < 0) {
                targetPosition = position - 1;
            } else {
                targetPosition = position + 1;
            }
        }

        if (layoutManager.canScrollVertically()) {
            if (velocityY < 0) {
                targetPosition = position - 1;
            } else {
                targetPosition = position + 1;
            }
        }

        val firstItem = 0;
        val lastItem = layoutManager.getItemCount() - 1;
        targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
        return targetPosition;
    }
}