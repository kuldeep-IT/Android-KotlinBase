package com.peerbits.base.network

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import com.peerbits.base.R
import com.peerbits.base.network.listeners.DefaultActionPerformer
import com.peerbits.base.network.listeners.NoInternetListner
import com.peerbits.base.network.listeners.RetrofitRawArrayResponseListener
import com.peerbits.base.network.listeners.RetrofitRawResponseListener
import com.peerbits.base.network.listeners.RetrofitResponseListener
import com.peerbits.base.ui.dialog.MessageDialog
import com.peerbits.base.utils.Utils
import com.peerbits.base.BuildConfig
import com.peerbits.base.utils.pref.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.peerbits.base.ui.base.BaseActivity
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.TimeUnit

class NetworkCall constructor(context: Context) : Callback<ResponseBody> {

    private var customBaseURL: String? = null

    private var REQUEST_TYPE = REQUEST_TYPE_POST

    private var endPoint = ""
    private var mContext: Context? = null

    private var shouldPromptOnNoInternet = true
    private var noInternetPromptType = NO_INTERNET_PROMPT_ALERT
    private var snackbarView: View? = null
    private var noInternetListner: NoInternetListner? = null

    private var retrofitResponseListener: RetrofitResponseListener? = null
    private var retrofitRawArrayResponseListener: RetrofitRawArrayResponseListener? = null

    private var retrofitRawResponseListener: RetrofitRawResponseListener? = null

    private var requestObject: Any? = null
    private var requestParams: HashMap<String, String>? = HashMap()
    private var pathParams: String? = null
    private var requestFiles: HashMap<String, File>? = null
    private var headers = HashMap<String, String>()

    private var call: Call<ResponseBody>? = null

    private val printBuilder = StringBuilder("\n API EndPoint : ")

    private var isMultipartCall = false
    private var isCustomResponse = false
    private var isFormUrlEncoded = false
    private var session = SessionManager(context)

    /* Internet Handeling*/
    private val isConnectedToInternet: Boolean
        get() {
            var result = false
            val connectivityManager =
                mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }
            return result
        }

    private//.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    val instance: ApiInterface
        get() {

            if (customBaseURL != null) {
                val interceptor = HttpLoggingInterceptor()
                if (BuildConfig.DEBUG) {
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                } else {
                    interceptor.level = HttpLoggingInterceptor.Level.NONE
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(customBaseURL!!)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retrofit.create(ApiInterface::class.java)
            } else {
                if (apiInterface == null) {
                    val interceptor = HttpLoggingInterceptor()
                    if (BuildConfig.DEBUG) {
                        interceptor.level = HttpLoggingInterceptor.Level.BODY
                    } else {
                        interceptor.level = HttpLoggingInterceptor.Level.NONE
                    }

                    val client = OkHttpClient.Builder()
                        .connectTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(50, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .addInterceptor(interceptor).build()

                    val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    apiInterface = retrofit.create(ApiInterface::class.java)
                }

                return apiInterface as ApiInterface
            }
        }

    internal val defaultMessageError: ArrayList<String>
        get() {
            val message = ArrayList<String>()
            message.add(mContext!!.getString(R.string.str_something_went_wrong))
            return message
        }

    init {
        this.mContext = context
    }

    fun setEndPoint(endPoint: String): NetworkCall {
        this.endPoint = endPoint
        return this
    }

    fun setHeaderFormCode(isFormUrlEncoded: Boolean): NetworkCall {
        this.isFormUrlEncoded = isFormUrlEncoded
        return this
    }

    fun setNoInternetPromptType(noInternetPromptType: Int): NetworkCall {
        this.noInternetPromptType = noInternetPromptType
        return this
    }

    fun setNoInternetPromptType(noInternetPromptType: Int, snackBarView: View): NetworkCall {
        this.noInternetPromptType = noInternetPromptType
        this.snackbarView = snackBarView
        return this
    }

    fun shouldPromptOnNoInternet(shouldPromptOnNoInternet: Boolean): NetworkCall {
        this.shouldPromptOnNoInternet = shouldPromptOnNoInternet
        return this
    }

    fun setNoInternetListner(noInternetListner: NoInternetListner): NetworkCall {
        this.noInternetListner = noInternetListner
        return this
    }

    fun setResponseListener(retrofitRxResponseListener: RetrofitResponseListener): NetworkCall {
        this.retrofitResponseListener = retrofitRxResponseListener
        return this
    }

    fun setRetrofitRawResponseListener(retrofitRawResponseListener: RetrofitRawResponseListener): NetworkCall {
        this.retrofitRawResponseListener = retrofitRawResponseListener
        return this
    }

    fun setRetrofitRawArrayResponseListener(retrofitRawArrayResponseListener: RetrofitRawArrayResponseListener): NetworkCall {
        this.retrofitRawArrayResponseListener = retrofitRawArrayResponseListener
        return this
    }

    fun setRequestObject(requestObject: Any): NetworkCall {
        this.requestObject = requestObject
        return this
    }

    fun setRequestParams(params: HashMap<String, String>): NetworkCall {
        this.requestParams = params
        return this
    }

    fun setPathParams(params: String): NetworkCall {
        this.pathParams = params
        return this
    }

    fun setHeaders(headers: HashMap<String, String>): NetworkCall {
        this.headers = headers
        return this
    }

    fun setFiles(fileParams: HashMap<String, File>): NetworkCall {
        this.requestFiles = fileParams
        this.isMultipartCall = true
        return this
    }

    fun setMultipartCall(multipartCall: Boolean) {
        isMultipartCall = multipartCall
    }

    fun setIsCustomResponse(isCustomResponse: Boolean): NetworkCall {
        this.isCustomResponse = isCustomResponse
        return this
    }

    private fun showNoInternetAlert() {
        val dialog = MessageDialog(mContext as BaseActivity<*, *>)
            .setTitle(mContext?.getString(R.string.str_no_internet))
            .setMessage(mContext?.getString(R.string.str_no_internet_message)).cancelable(true)
            .setPositiveButton(mContext?.getString(R.string.ok)) { d1, i ->
                d1.dismiss()
            }
        dialog.show()
    }

    fun makeEmptyRequestCall() {
        requestParams = HashMap()
        makeCall()
    }

    fun makeCall(): NetworkCall {

        if (requestObject == null && requestParams == null) {
            Timber.e("Error", "No Request Source is Provided")
        } else {
            if (isConnectedToInternet) {

                Timber.e("Error", "API EndPoint => $endPoint")
                printBuilder.append(endPoint).append("\n\n").append("Headers\n")

                if (actionPerformer != null) {
                    actionPerformer!!.onActionPerform(headers, requestParams!!)
                }

                if (isFormUrlEncoded) {
                    headers["Content-Type"] = "application/x-www-form-urlencoded"
                }

                if (headers.size > 0) {
                    for ((key, value) in headers) {
                        Timber.e("Error", "$key=>$value")
                        printBuilder.append(key).append("=>").append(value).append("\n")
                    }
                } else {
                    Timber.e("Error", "headers are empty")
                    printBuilder.append("Headers are Empty")
                }


                if (retrofitResponseListener != null) {
                    retrofitResponseListener!!.onPreExecute()
                }


                if (retrofitRawResponseListener != null) {
                    retrofitRawResponseListener!!.onPreExecute()
                }

                if (retrofitRawArrayResponseListener != null) {
                    retrofitRawArrayResponseListener!!.onPreExecute()
                }


                if (requestObject != null) {
                    makeRequestWithObject(requestObject!!)
                } else {
                    makeRequestWithParams(requestParams!!)
                }
            } else {
                if (shouldPromptOnNoInternet) {
                    when (noInternetPromptType) {
                        NO_INTERNET_PROMPT_TOAST -> Toast.makeText(
                            mContext,
                            mContext!!.getText(R.string.str_no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                        NO_INTERNET_PROMPT_SNACKBAR -> if (snackbarView != null) {
                            Snackbar.make(
                                snackbarView!!,
                                mContext!!.getText(R.string.str_no_internet),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        NO_INTERNET_PROMPT_ALERT -> showNoInternetAlert()
                    }
                }
                if (noInternetListner != null) {
                    noInternetListner!!.onNoInternet()
                }
            }
        }

        return this
    }

    fun cancelRequest() {
        call!!.cancel()
    }

    private fun makeRequestWithObject(requestClass: Any) {

        printBuilder.append("\n\n").append("Request Object\n")
        printBuilder.append(requestClass.toString()).append("\n\n")
        Timber.e("Error", requestClass.toString())
        when (REQUEST_TYPE) {
            REQUEST_TYPE_GET -> call = instance.APICall(endPoint, headers)
            REQUEST_TYPE_PUT -> call = instance.APIPutCall(endPoint, headers, requestClass)
            else -> call = instance.APICall(endPoint, headers, requestClass)
        }
        call!!.enqueue(this)
    }

    private fun makeRequestWithParams(requestParams: HashMap<String, String>) {

        printBuilder.append("\n\n").append("Request Params\n")

        if (isMultipartCall) {
            val bodyParams = HashMap<String, RequestBody>()
            var body: RequestBody? = null

            if (requestParams.size > 0) {
                for ((key, value) in requestParams) {
                    Timber.e("Error", "$key=>$value")
                    printBuilder.append(key).append("=>").append(value).append("\n")
                    bodyParams[key] = createPartFromString(value)
                }
            } else {
                Timber.e("Error", "Param are empty")
                printBuilder.append(" Params are empty ")
            }


            if (requestFiles != null && requestFiles!!.size > 0) {
                printBuilder.append("\n\n").append("Files to Upload\n")
                for ((key, value) in requestFiles!!) {
                    Timber.e("Error", key + "=>" + value.path)
                    printBuilder.append(key).append("=>").append(value.path).append("\n")
                    val fileName = key + "\"; filename=\"" + value.name
                    bodyParams[fileName] = createPartFromFile(value)
                    body = createPartFromFileBinary(value)
                }
            }

            when (REQUEST_TYPE) {
                REQUEST_TYPE_BINARY -> call = instance.APIBinaryCall(endPoint, body!!)
                /*  REQUEST_TYPE_OLD_POLICY -> call =
                      instance.APIOldPolicies(*//*session.getUser()?.userId.toString()*//*"128", headers)*/
                /*REQUEST_TYPE_VEHICLE_YEAR -> call =
                    instance.APIVehicleYear(pathParams ?: "", headers)*/
                else -> call = instance.APIMultipartCall(endPoint, headers, bodyParams)
            }
            call!!.enqueue(this)
        } else {

            if (requestParams.size > 0) {
                for ((key, value) in requestParams) {
                    Timber.e("Error", "$key=>$value")
                    requestParams[key] = value.trim { it <= ' ' }
                    printBuilder.append(key).append("=>").append(value).append("\n")
                }
            } else {
                Timber.e("Error", "Param are empty")
                printBuilder.append(" Params are empty ")
            }
            val session = SessionManager(mContext!!)
            when (REQUEST_TYPE) {
                REQUEST_TYPE_GET -> call = instance.APICall(endPoint, headers)
                /*  REQUEST_TYPE_OLD_POLICY -> call =
                      instance.APIOldPolicies(*//*session.getUser()?.userId.toString()*//*"128", headers)*/
                /*REQUEST_TYPE_VEHICLE_YEAR -> call =
                    instance.APIVehicleYear(pathParams ?: "", headers)*/
                else -> call = instance.APICall(endPoint, headers, requestParams)
            }

            call!!.enqueue(this)
        }
    }

    private fun createPartFromFile(file: File): RequestBody {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file)
    }

    private fun createPartFromFileBinary(file: File): RequestBody {
        return RequestBody.create(
            MediaType.parse(MULTIPART_BINARY_DATA),
            FileUtils.readFileToByteArray(file)
        )
        // return RequestBody.create(MediaType.parse(MULTIPART_BINARY_DATA), file)
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        handleResponse(response)
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        handleError(t)

        printBuilder.append("\n\nResponse\n")
        printBuilder.append("Call to the API Failed")
        printBuilder.append("\n\nThank you\n\n")
        copyToClipBoard()
    }

    private fun handleResponse(response: Response<ResponseBody>) {

        if (response.code() == 401) {
            clearSessionAndOpenLogin(mContext)
            return
        }

        printBuilder.append("\n\nStatusCode : ").append(response.code().toString()).append("")

        try {
            if (response.body() != null) {

                val body = response.body()!!.string()
                Timber.e("Error", "Success Response : $body")

                printBuilder.append("\n\nResponse\n")
                printBuilder.append(body)
                printBuilder.append("\n\n Thank you\n\n")

                copyToClipBoard()
                var jsonObject: JSONObject
                try {
                    if (response.code() == 422) {
                        if (retrofitRawResponseListener != null) {
                            retrofitRawResponseListener!!.onSuccess(
                                response.code(),
                                JSONObject(),
                                body
                            )
                        }
                    } else {
                        jsonObject = JSONObject(body)
                        if (isCustomResponse) {
                            if (retrofitRawResponseListener != null) {
                                retrofitRawResponseListener!!.onSuccess(
                                    response.code(),
                                    jsonObject,
                                    body
                                )
                            }
                        } else
                            if (retrofitRawResponseListener != null) {
                                if (jsonObject.has("Success"))
                                    if (jsonObject.optBoolean("Success"))
                                        retrofitRawResponseListener!!.onSuccess(
                                            response.code(),
                                            jsonObject.optJSONObject("data"),
                                            body
                                        )
                                    else {
                                        if (jsonObject.has("PolicyDetails") && (jsonObject.opt("PolicyDetails") as JSONObject).has(
                                                "StatusList"
                                            )
                                        ) {
                                            retrofitRawResponseListener!!.onSuccess(
                                                response.code(),
                                                jsonObject,
                                                body
                                            )
                                        } else {
                                            val message =
                                                Utils.getMessageFromResponseObject(
                                                    mContext!!,
                                                    jsonObject
                                                )
                                            retrofitRawResponseListener!!.onError(
                                                response.code(),
                                                message
                                            )
                                        }
                                    }
                                else {
                                    retrofitRawResponseListener!!.onSuccess(
                                        response.code(),
                                        jsonObject,
                                        body
                                    )
                                }
                            }
                        if (jsonObject.optInt("success") == 1 || jsonObject.optBoolean("success") == true) {
                            if (retrofitResponseListener != null) {
                                retrofitResponseListener!!.onSuccess(
                                    response.code(),
                                    jsonObject.optJSONObject("data"),
                                    body
                                )
                            }
                        } else {
                            val errorMessage =
                                Utils.getMessageFromResponseObject(mContext!!, jsonObject)
                            if (retrofitResponseListener != null) {
                                retrofitResponseListener!!.onError(response.code(), errorMessage)
                            }
                        }
                    }
                } catch (e: Exception) {
                    var bodyString = body.toString()
                    if (bodyString.startsWith("\"https") || bodyString.startsWith("\"<html>")) {
                        if (response.code() == 200)
                            retrofitRawResponseListener?.onSuccess(
                                response.code(),
                                JSONObject(),
                                bodyString
                            )
                    } else {
                        try {
                            val jsonArray: JSONArray
                            jsonArray = JSONArray(body)
                            if (retrofitRawArrayResponseListener != null) {
                                retrofitRawArrayResponseListener!!.onSuccess(
                                    response.code(),
                                    jsonArray,
                                    body
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                val body = response.errorBody()!!.string()
                Timber.e("Error", "Error Body =>$body")

                printBuilder.append("\n\nResponse\n")
                printBuilder.append(body)
                printBuilder.append("\n\nThank you\n\n")
                copyToClipBoard()

                var message = defaultMessageError

                // This code just to handle error response which is not there in our application still it is there for future purpose
                if (response.code() == 422) {
                    if (retrofitRawResponseListener != null) {
                        retrofitRawResponseListener!!.onSuccess(
                            response.code(),
                            JSONObject(),
                            body
                        )
                    }
                } else
                    try {
                        val `object` = JSONObject(body)
                        message = Utils.getMessageFromResponseObject(mContext!!, `object`)
                    } catch (e: JSONException) {
                        Timber.e("ERROR ->", e.message)
                        e.printStackTrace()
                    } finally {

                        if (retrofitRawResponseListener != null) {
                            retrofitRawResponseListener!!.onError(response.code(), message)
                        }

                        if (retrofitResponseListener != null) {
                            retrofitResponseListener!!.onError(response.code(), message)
                            if (response.code() == 401) {
                                Toast.makeText(
                                    mContext,
                                    R.string.str_session_expried_msg,
                                    Toast.LENGTH_SHORT
                                ).show()

                                /* val sessionManager = SessionManager(mContext)
                                 sessionManager.clearSession()
                                 sessionManager.setFlagFromKey(PreferenceKeys.KEY_IS_FIRST_TIME, true)
                                 sessionManager.setFlagFromKey( PreferenceKeys.KEY_IS_FIRST_TIME_INVITE,true )
                                val intent = Intent(mContext, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                mContext!!.startActivity(intent)
                                (mContext as AppCompatActivity).finish()*/

                                clearSessionAndOpenLogin(mContext)
                            }
                        }
                    }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.e("ERROR ->", e.message)
            if (retrofitResponseListener != null) {
                retrofitResponseListener!!.onError(response.code(), defaultMessageError)
            }

            if (retrofitRawResponseListener != null) {
                retrofitRawResponseListener!!.onError(response.code(), defaultMessageError)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Timber.e("ERROR ->", e.message)
            if (retrofitResponseListener != null) {
                retrofitResponseListener!!.onError(response.code(), defaultMessageError)
            }
            if (retrofitRawResponseListener != null) {
                retrofitRawResponseListener!!.onError(response.code(), defaultMessageError)
            }
        }
    }

    private fun handleError(e: Throwable) {
        e.printStackTrace()
        Timber.e("ERROR ->", e.message)
        if (retrofitResponseListener != null) {
            retrofitResponseListener!!.onError(500, defaultMessageError)
        }
        if (retrofitRawResponseListener != null) {
            retrofitRawResponseListener!!.onError(500, defaultMessageError)
        }
    }

    private fun copyToClipBoard() {
        if (isDubuggable) {
            val clipboard =
                mContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("API Data", printBuilder.toString())
            assert(clipboard != null)
            clipboard.setPrimaryClip(clip)
        }
    }

    fun setCustomBaseURL(customBaseURL: String): NetworkCall {
        this.customBaseURL = customBaseURL
        return this
    }

    private fun createPartFromString(value: String): RequestBody {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), value)
    }

    fun setRequestType(requestType: Int): NetworkCall {
        REQUEST_TYPE = requestType
        return this
    }

    private fun clearSessionAndOpenLogin(context: Context?) {
        session.clearSession()
    }

    companion object {

        private const val MULTIPART_FORM_DATA = "multipart/form-data"
        private const val MULTIPART_BINARY_DATA = "application/octet-stream"
        const val NO_INTERNET_PROMPT_TOAST = 0
        const val NO_INTERNET_PROMPT_SNACKBAR = 1
        const val NO_INTERNET_PROMPT_ALERT = 2
        const val REQUEST_TYPE_POST = 0
        const val REQUEST_TYPE_GET = 1
        const val REQUEST_TYPE_BINARY = 2
        const val REQUEST_TYPE_DELETE = 3
        const val REQUEST_TYPE_PUT = 4

        private var BASE_URL = BuildConfig.BASE_URL // this will be affected by flavor in gradle

        private var actionPerformer: DefaultActionPerformer? = null
        private var isDubuggable = false

        fun with(context: Context): NetworkCall {
            return NetworkCall(context)
        }

        fun setActionPerformer(actionPerformer: DefaultActionPerformer) {
            Companion.actionPerformer = actionPerformer
        }

        fun setIsDubuggable(isDubuggable: Boolean) {
            Companion.isDubuggable = isDubuggable
        }

        // Retrofit API Interface Instance Handling
        private var apiInterface: ApiInterface? = null

        fun setBASE_URL(BASE_URL: String) {
            Companion.BASE_URL = BASE_URL
        }
    }
}



