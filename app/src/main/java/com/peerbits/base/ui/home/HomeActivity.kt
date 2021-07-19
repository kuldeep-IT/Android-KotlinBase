package com.peerbits.base.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.peerbits.base.R
import com.peerbits.base.adapter.HomeAdapter
import com.peerbits.base.model.HomeModel
import com.peerbits.base.network.API_END_POINTS
import com.peerbits.base.network.NetworkCall
import com.peerbits.base.network.listeners.RetrofitResponseListener
import com.peerbits.base.utils.pref.SessionManager
import kotlinx.android.synthetic.main.activity_home.rvList
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar.view.tvTitle
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.inject
import java.util.ArrayList
import java.util.HashMap

class HomeActivity : AppCompatActivity() {

    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setDatainPreference()
        apiCallHomeData()
        setToolbar()
    }

    private fun setDatainPreference() {
        sessionManager.setValueFromKey("VALUE", "THIS IS TO TEST IF INJECT WORKS")
        Toast.makeText(this, sessionManager.getValueFromKey("VALUE", ""), Toast.LENGTH_LONG).show()
    }

    fun setToolbar() {
        toolbar.tvTitle.text = "HOME"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun apiCallHomeData() {
        val params = HashMap<String, String>()
        NetworkCall.with(this)
            .setRequestParams(params)
            .setEndPoint(API_END_POINTS.HOME_DATA)
            .setResponseListener(object : RetrofitResponseListener {
                override fun onPreExecute() {
                    // showProgress()
                }

                override fun onSuccess(statusCode: Int, jsonObject: JSONObject, response: String) {
                    //stopProgress()
                    val homeData =
                        Gson().fromJson<HomeModel>(jsonObject.toString(), HomeModel::class.java)
                    setHomeData(homeData)
                }

                override fun onError(statusCode: Int, messages: ArrayList<String>) {
                    //stopProgress()
                }
            }).makeCall()
    }

    private fun setHomeData(homeData: HomeModel?) {
        rvList.layoutManager = LinearLayoutManager(this)
        val adapter = HomeAdapter(this, homeData?.homeData!!, rvList)
        adapter.notifyDataSetChanged()
        rvList.adapter = adapter
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivityNew::class.java)
            return intent
        }
    }
}