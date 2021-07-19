package com.peerbits.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ReportFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.peerbits.base.R
import com.peerbits.base.model.HomeData
import com.peerbits.base.ui.base.BaseRecyclerViewAdapter
import com.peerbits.base.ui.base.BaseViewHolder
import com.peerbits.base.utils.GlideLoader
import kotlinx.android.synthetic.main.list_items.view.ivImage
import kotlinx.android.synthetic.main.list_items.view.tvCaption
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeAdapter(context: Context, arrayList: ArrayList<HomeData>, recyclerView: RecyclerView?) :
    BaseRecyclerViewAdapter<HomeAdapter.HomeViewHolder, HomeData>(), KoinComponent {
    private var mData: List<HomeData>
    private val mContext: Context
    private val glide: GlideLoader by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.getContext())
        val view: View = inflater.inflate(R.layout.list_items, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class HomeViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int) {
            itemView.tvCaption.text = mData[position].caption
            glide.loadImageSimple(mData[position].image!!, itemView.ivImage)
        }
    }

    //private GlideLoader glideLoader;
    init {
        mData = arrayList
        mContext = context
        //glideLoader = new GlideLoader(mContext);
    }
}