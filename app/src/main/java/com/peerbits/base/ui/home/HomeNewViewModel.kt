package com.peerbits.base.ui.home

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.peerbits.base.AppClass
import com.peerbits.base.model.HomeData
import com.peerbits.base.model.HomeModel
import com.peerbits.base.network.API_END_POINTS
import com.peerbits.base.network.NetworkCall
import com.peerbits.base.network.listeners.RetrofitResponseListener
import com.peerbits.base.ui.base.BaseNavigator
import com.peerbits.base.ui.base.BaseViewModel
import org.json.JSONObject
import java.util.HashMap

class HomeNewViewModel(app: AppClass) : BaseViewModel<BaseNavigator>(app) {
    val name = ObservableField("")
    var arrImages = MutableLiveData<ArrayList<HomeData>>(arrayListOf())

    fun apiCallHomeData(onApiResult : (responseSuccess : String)-> Unit) {
        val params = HashMap<String, String>()
        NetworkCall.with(appContext)
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
                    arrImages.value = homeData.homeData
                    onApiResult("This is success")
                }

                override fun onError(statusCode: Int, messages: java.util.ArrayList<String>) {
                    //stopProgress()
                }
            }).makeCall()
    }
}