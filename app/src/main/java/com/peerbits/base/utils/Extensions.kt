package com.peerbits.base.utils

import android.content.Context
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.MutableLiveData

fun View.visible(value: Boolean) {
    visibility = if (value) VISIBLE else GONE
}

fun View.invisible(value: Boolean) {
    visibility = if (value) INVISIBLE else VISIBLE
}

fun View.istVisible(): Boolean {
    return visibility == VISIBLE
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}
/**
 * Changing tabs font
 */
/*fun TabLayout.changeFont(@FontRes font: Int = R.font.regular) {

    val tabsCount = childCount
    for (j in 0 until tabsCount) {
        val vgTab = getChildAt(j) as ViewGroup
        val tabChildsCount = vgTab.childCount
        for (i in 0 until tabChildsCount) {
            val tabViewChild = vgTab.getChildAt(i)
            val flag = tabViewChild is TextView
            Timber.d("INstance", flag.toString())
            if (tabViewChild is TextView) {
                tabViewChild.typeface = ResourcesCompat.getFont(context, font)
            }
        }
    }
}*/


/**
 * hide soft keyboard
 */
fun View.hideKeyboard() {
    val view = this
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}