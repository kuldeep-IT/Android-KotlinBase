package com.peerbits.base.ui.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : BaseViewHolder, M>(val recyclerView: RecyclerView? = null) :
    RecyclerView.Adapter<T>() {

    var isLoading: Boolean = false

    private val visibleThreshold = 1
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0

    init {
        val linearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager?
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager?.itemCount!!
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    /*
                     End has been reached Do something
                   */
                    onLoadMoreListener?.onLoadMore()
                    isLoading = true
                }
            }
        })
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: T, position: Int) {
        holder.onBind(position)
    }

    var list: ArrayList<M?> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnItemClickListener<M> {

        fun onClick(position: Int, data: M)
    }

    interface OnItemClickListenerMultiple<M> {

        fun onClick(position: Int, childPosition: Int, data: M)
    }

    interface OnLoadMoreListener {

        fun onLoadMore()
    }

    fun setLoaded() {
        isLoading = false
    }

    var onItemClickListener: OnItemClickListener<M>? = null
    var onItemMultipleClickListener: OnItemClickListenerMultiple<M>? = null

    var onLoadMoreListener: OnLoadMoreListener? = null

}