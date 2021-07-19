package com.nlgic.insurance.retrofit_call

import com.peerbits.base.retrofit_call.RetrofitCall
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class RetrofitCallAdapterFactory private constructor() : CallAdapter.Factory() {
    override operator fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        if (rawType == RetrofitCall::class.java && returnType is ParameterizedType) {
            val callReturnType = getParameterUpperBound(0, returnType)
            return RetrofitCallAdapter<Any>(callReturnType)
        }
        return null
    }

    companion object {

        private var instance: RetrofitCallAdapterFactory? = null

        @Synchronized
        fun getInstance(): RetrofitCallAdapterFactory {
            if (instance == null) {
                instance = RetrofitCallAdapterFactory()
            }
            return instance!!
        }
    }
}