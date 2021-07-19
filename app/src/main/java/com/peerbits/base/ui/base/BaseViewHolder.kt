package com.peerbits.base.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ak on 10/07/18.
 */

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position: Int)
}
