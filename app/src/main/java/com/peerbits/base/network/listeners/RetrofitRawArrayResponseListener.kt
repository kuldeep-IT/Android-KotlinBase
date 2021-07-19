package com.peerbits.base.network.listeners

import org.json.JSONArray

import java.util.ArrayList


interface RetrofitRawArrayResponseListener {
    fun onPreExecute()
    fun onSuccess(statusCode: Int, jsonObject: JSONArray, response: String)
    fun onError(statusCode: Int, messages: ArrayList<String>)
}
