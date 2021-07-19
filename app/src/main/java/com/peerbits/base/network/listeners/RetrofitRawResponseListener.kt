package com.peerbits.base.network.listeners

import org.json.JSONObject

import java.util.ArrayList


interface RetrofitRawResponseListener {
    fun onPreExecute()
    fun onSuccess(statusCode: Int, jsonObject: JSONObject, response: String)
    fun onError(statusCode: Int, messages: ArrayList<String>)
}
