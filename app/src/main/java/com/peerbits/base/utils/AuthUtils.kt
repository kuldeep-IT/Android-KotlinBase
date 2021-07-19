package com.peerbits.base.utils

import android.util.Base64

/**
 * Static utilities for common authentication operations.
 * Created by skidson on 16-04-27.
 */
object AuthUtils {

    /**
     * Encodes the emailOrMobile and password as defined by RFC2045 and prepends "Basic ".
     */
    fun encodeForBasicAuthentication(username: String, password: String): String {
        val basic = (username + ":" + password).toByteArray()
        return "Basic " + Base64.encodeToString(basic, Base64.URL_SAFE or Base64.NO_WRAP)
    }
}
