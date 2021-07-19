package com.peerbits.base

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Callback class that is the base of all Retrofit callbacks in the app.
 * Created by Mauro on 2017-08-11.
 */
abstract class BaseCallback<T>() : Callback<T> {


    override fun onResponse(call: Call<T>?, response: Response<T>?) {

            when {
                response?.isSuccessful == true -> onResponseSuccess(call, response)
                response?.code() != HttpURLConnection.HTTP_INTERNAL_ERROR -> onResponseError(call, response)
                else -> onResponseServerError(call, response)
            }

    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {

            when (t) {
                is SocketTimeoutException -> {
                }
                is SSLPeerUnverifiedException ->{}
                is SocketException -> {}
                is IOException -> {}
                else -> {}
            }
        }



    /**
     * Success responses (i.e. 200 error code) are mapped through this function. Note that implementation
     * is optional.
     */
    open fun onResponseSuccess(call: Call<T>?, response: Response<T>?) {}

    /**
     * Error responses (i.e. 4XX error code) are mapped through this function. Note that server
     * error responses (i.e. 500) are not trickled down. Note also that the onFailure function is
     * handled at this layer and not trickled down. Note that implementation is optional.
     */
    open fun onResponseError(call: Call<T>?, response: Response<T>?) {}

    open fun onResponseServerError(call: Call<T>?, response: Response<T>?) {
    }

    companion object {
        private val TAG = BaseCallback::class.java.simpleName
    }
}