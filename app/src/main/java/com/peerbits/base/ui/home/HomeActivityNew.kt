package com.peerbits.base.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.peerbits.base.BR
import com.peerbits.base.R
import com.peerbits.base.ViewModelProviderFactory
import com.peerbits.base.adapter.HomeAdapter
import com.peerbits.base.databinding.ActivityHomeNewBinding
import com.peerbits.base.model.HomeModel
import com.peerbits.base.network.API_END_POINTS
import com.peerbits.base.network.NetworkCall
import com.peerbits.base.network.listeners.RetrofitResponseListener
import com.peerbits.base.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.rvList
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class HomeActivityNew : BaseActivity<ActivityHomeNewBinding, HomeNewViewModel>() {
    override val viewModel: HomeNewViewModel
        get() = ViewModelProvider(
            this, ViewModelProviderFactory<HomeNewViewModel>(HomeNewViewModel(app))
        ).get(HomeNewViewModel::class.java)
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_home_new

    override fun initialization(savedInstance: Bundle?) {
        viewModel.navigator = this
        init()
        viewModel.apiCallHomeData {
            showToast(it)
        }
    }

    fun init() {
        // viewModel.name.set("Shahnavaz Ansari")
        rvList.layoutManager = LinearLayoutManager(this)
        val adapter = HomeAdapter(this, viewModel.arrImages.value!!, rvList)
        adapter.notifyDataSetChanged()
        rvList.adapter = adapter
        viewModel.arrImages.observe(this, Observer {
            adapter.setList(it)
        })
    }
}