package com.peerbits.base.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.peerbits.base.R

/**
 * Created by ak on 07/07/18.
 */

object ViewUtils {

    /**
     * Converts density independent pixels to their raw pixel value based on the current device's
     * display metrics.
     */
    fun dpToPx(resources: Resources, dip: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dip,
            resources.displayMetrics)
    }

    fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (v.translationX + 0.5f).toInt()
        val ty = (v.translationY + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return x >= left && x <= right && y >= top && y <= bottom
    }

    /**
     * Converts scaled pixels to their raw pixel value based on the current device's display metrics.
     */
    fun spToPx(resources: Resources, sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp,
            resources.displayMetrics)
    }

    fun pxToDp(px: Float): Float {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
        return px / (densityDpi / 160f)
    }

    fun dpToPx(dp: Float): Int {
        val density = Resources.getSystem().displayMetrics.density
        return Math.round(dp * density)
    }

    fun changeIconDrawableToGray(context: Context, drawable: Drawable?) {
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(ContextCompat
                    .getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
        }
    }


    /**
     * Closes the soft keyboard if open.
     * @param inputView a view in the same window as the input control that prompted the keyboard
     * *                  open (usually an [android.widget.EditText] but not required to be).
     */
    fun closeSoftKeyboard(inputView: View) {
        val imm = inputView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputView.windowToken, 0)
    }

    /**
     * Opens the soft keyboard if closed.
     * @param inputView a view in the same window that should prompt the keyboard open
     * *                  (usually an [android.widget.EditText] but not required to be).
     */
    fun showSoftKeyboard(inputView: View) {
        inputView.requestFocus()
        val imm = inputView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputView, 0)
        val listener = KeyboardViewLayoutListener(inputView)
        inputView.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private class KeyboardViewLayoutListener (private val view: View) : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        }
    }

}// This class is not publicly instantiable
