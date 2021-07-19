package com.nlgic.insurance.retrofit_call

import com.peerbits.base.retrofit_call.RetrofitCall
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class RetrofitCallAdapter<T>(private val responseType: Type) :
    CallAdapter<T, RetrofitCall<T>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<T>): RetrofitCall<T> {
        return RetrofitCall(call)
    }
}