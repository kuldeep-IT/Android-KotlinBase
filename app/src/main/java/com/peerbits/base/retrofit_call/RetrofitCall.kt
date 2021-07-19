package com.peerbits.base.retrofit_call

import android.os.Handler
import android.os.Looper
import androidx.annotation.WorkerThread
import com.peerbits.base.utils.CommonUtils
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.Executor


class ApiException(override val message: String?, val response: String?) : Exception()

class RetrofitCall<T>(private val call: Call<T>) : Call<T> {
    private val logTag = RetrofitCall::class.java.simpleName

    val executor = UiThreadExecutor()

    inner class UiThreadExecutor : Executor {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    @Throws(IOException::class)
    override fun execute(): Response<T> {
        return call.execute()
    }

    override fun enqueue(callback: Callback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {


                if (response.isSuccessful && response.errorBody() == null) {

                    handleSuccessResponse(call, response, callback)

                } else {

                    val message = CommonUtils.parseErrorResponse(response.errorBody()?.string())
                    handleErrorResponse(
                        call,
                        ApiException(message, response.errorBody()?.string()),
                        callback
                    )

                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                handleErrorResponse(call, t, callback)
            }
        })
    }

    @WorkerThread
    private fun handleErrorResponse(call: Call<T>?, t: Throwable, callback: Callback<T>) {

        executor.execute(Runnable {
            if (null == call || call.isCanceled) {
                return@Runnable
            }
            callback.onFailure(call, t)

        })
    }

    @WorkerThread
    private fun handleSuccessResponse(
        call: Call<T>?,
        response: Response<T>,
        callback: Callback<T>
    ) {


        executor.execute(Runnable {
            if (null == call || call.isCanceled) {
                return@Runnable
            }

            callback.onResponse(call, response)

        })
    }

    override fun isExecuted(): Boolean {
        return call.isExecuted
    }

    override fun cancel() {
        call.cancel()
    }

    override fun isCanceled(): Boolean {
        return call.isCanceled
    }

    override fun clone(): RetrofitCall<T> {
        val clone = RetrofitCall(call.clone())

        return clone
    }

    override fun request(): Request {
        return call.request()
    }

    private fun isCallSuccess(response: Response<T>): Boolean {
        val httpStatusCode = response.code()
        return httpStatusCode >= HttpURLConnection.HTTP_OK && httpStatusCode < HttpURLConnection.HTTP_BAD_REQUEST
    }
}