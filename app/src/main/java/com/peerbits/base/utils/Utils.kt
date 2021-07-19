package com.peerbits.base.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.EditText
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL
import java.net.URLDecoder
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.Map
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import timber.log.Timber

/**
 * Created by RajeshKushvaha on 24-05-17
 */

object Utils {

    private val ORIENTATIONS = SparseIntArray()

    var filterPreventWhiteSpaces: InputFilter =
        InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: Exception) {
            Timber.e("Tag", "getRealPathFromURI Exception : $e")
            return ""
        } finally {
            cursor?.close()
        }
    }

    fun dipToPixels(context: Activity, dipValue: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics).toInt()
    }

    fun getDurationInSecVideo(context: Context, file: File?): Long {
        if (file == null || !file.exists())
            return 0

        val retriever = MediaMetadataRetriever()
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        if (TextUtils.isEmpty(time))
            return 0
        val timeInMillisec = java.lang.Long.parseLong(time)

        retriever.release()
        return TimeUnit.MILLISECONDS.toSeconds(timeInMillisec)
    }

    fun getFileSizeInMB(file: File?): Long {
        if (file == null || !file.exists())
            return 0

        val fileSizeInBytes = file.length()
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        val fileSizeInKB = fileSizeInBytes / 1024
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        return fileSizeInKB / 1024
    }

    fun preventDoubleClick(view: View?) {
        view?.isEnabled = false
        Handler().postDelayed({ view?.isEnabled = true }, 2000)
    }

    /*fun preventDoubleClick(view: View) {
        // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
    }*/

    fun getMessageFromResponseObject(context: Context, `object`: JSONObject?): ArrayList<String> {
        val message = ArrayList<String>()

        if (`object` != null && `object`.has("error")) {
            val errors_arr = `object`.optJSONArray("error")
            try {
                for (i in 0 until errors_arr!!.length()) {
                    message.add(errors_arr.optString(i))
                }
            } catch (e: Exception) {

            }
        }
        if (`object` != null && `object`.has("Message")) {
            message.add(`object`.optString("Message"))
        }
        if (message.size == 0) {
            // message.add(context.getString(R.string.str_something_went_wrong))
        }
        return message
    }

    //*****************************************************************
    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$*!]).{8,15})"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
            .matches()
    }

    fun isValidMobile(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && target.length >= 7 && !TextUtils.isEmpty(target) && target.length <= 10
    }

    /**
     * Check if the device is connected to internet
     *
     * @return true if connected to internet false otherwise.
     */
    fun isConnectedToInternet(mContext: Context): Boolean {
        val connectivity =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivity.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
    }

    fun showSoftKeyboard(editText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard(mContext: Context) {
        try {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow((mContext as Activity).currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*fun getKMFromMeters(meters: String): String {
        return setDoubleDecimalPoints(AppConstants.DEFAULT_DECIMAL_POINTS, meters.toDouble() / 1000)
    }*/

    fun getMinutesFromSeconds(seconds: String): String {
        return (seconds.toInt() / 60).toString()
    }

    /**
     * We have to return decimal point count and double value
     */
    private fun setDoubleDecimalPoints(decimalPoints: Int, value: Double): String {
        return String.format("%." + decimalPoints + "f", value)
    }

    fun clearCookies(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Timber.d(
                "TAG",
                "Using clearCookies code for API >=" + Build.VERSION_CODES.LOLLIPOP_MR1.toString()
            )
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            Timber.d(
                "TAG",
                "Using clearCookies code for API <" + Build.VERSION_CODES.LOLLIPOP_MR1.toString()
            )
            val cookieSyncMngr = CookieSyncManager.createInstance(context)
            cookieSyncMngr.startSync()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }
    }

    fun fileExists(URLName: String): Boolean {
        try {
            HttpURLConnection.setFollowRedirects(false)
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            val con = URL(URLName).openConnection() as HttpURLConnection
            con.requestMethod = "HEAD"
            return con.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    @Throws(java.lang.Exception::class)
    fun convertHashMaptoQueryString(
        params: HashMap<String, String>
    ): String? {
        val sb = StringBuilder()
        val iter: Iterator<*> = params.entries.iterator()
        while (iter.hasNext()) {
            if (sb.length > 0) {
                sb.append('&')
            } //w  w  w. j ava2s .  c o m
            val entry: Map.Entry<*, *> = iter.next() as Map.Entry<*, *>
            sb.append(entry.getKey()).append("=").append(entry.getValue())
        }
        return sb.toString()
    }

    fun getQueryParams(url: String): HashMap<String, String> {
        try {
            val params = HashMap<String, String>()
            val urlParts = url.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (urlParts.size > 1) {
                val query = urlParts[1]
                for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()) {
                    val pair =
                        param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val key = URLDecoder.decode(pair[0], "UTF-8")
                    var value = ""
                    if (pair.size > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8")
                    }
                    params[key] = value
                    /*var values: MutableList<String>? = params[key] as MutableList<String>?
                    if (values == null) {
                        values = ArrayList()
                        params[key] = values
                    }
                    values.add(value)*/
                }
            }

            return params
        } catch (ex: UnsupportedEncodingException) {
            throw AssertionError(ex)
        }
    }

    /**App Running or not */
    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun foregrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE
    }

    fun isMyServiceRunning(activity: Activity, serviceClass: Class<*>): Boolean {
        val manager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    //Tp Convert Image URI into base64
    @Throws(IOException::class)
    fun getBase64Image(context: Context, uri: Uri, onImageSet: (base64String: String) -> Unit) {
        try {
            val iStream: InputStream? = context.getContentResolver().openInputStream(uri)
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (iStream?.read(buffer).also({ len = it!! }) != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            val encodedImage: String =
                Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT)
            onImageSet(encodedImage)
        } catch (e: Exception){
            e.printStackTrace()
            onImageSet(uri.toString())
        }
    }

    fun getBase64Image(bitmap: Bitmap, onImageSet: (base64String: String) -> Unit) {
        var byteArrayOutputStream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        var byteArray = byteArrayOutputStream?.toByteArray()
        val encodedImage: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        onImageSet(encodedImage)
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    fun getIPAddress(useIPv4: Boolean = true): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.getInetAddresses())
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress()) {
                        val sAddr: String = addr.getHostAddress()
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                    0,
                                    delim
                                ).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: java.lang.Exception) {
        } // for now eat exceptions
        return ""
    }
}
