package com.peerbits.base.utils

import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import java.util.regex.Pattern

/**
 * Static utilities for local field validation.
 * Created by skidson on 16-04-28.
 */
@Suppress("UseExpressionBody")
object ValidationUtils {

    private val USER_NAME = Pattern.compile("[a-z0-9_-]{3,15}")
    private val EMAIL = Patterns.EMAIL_ADDRESS
    private val PHONE_NUMBER = Patterns.PHONE
    private val USER_LOOKUP =
        Pattern.compile("^(($USER_NAME)|($EMAIL)|($PHONE_NUMBER)) (($USER_NAME)|($EMAIL)|($PHONE_NUMBER))\$")


    private val regexEmail = (
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(regexEmail, Pattern.CASE_INSENSITIVE)
    private val regexUserName = "^[a-zA-Z0-9_-]*$"

    private val USERNAME_PATTERN = Pattern.compile(regexUserName, Pattern.CASE_INSENSITIVE)

    /**
     * Check EditText is Empty or not
     * Input:
     * 1. EditText
     * Output: True / False
     */
    fun isFieldEmpty(et: EditText): Boolean {
        return TextUtils.isEmpty(et.text.toString().trim { it <= ' ' })
    }

    /**
     * Check String is Empty or not
     * Input:
     * 1. String
     * Output: True / False
     */
    fun isEditTextEmpty(text: String?): Boolean {
        return text == null || TextUtils.isEmpty(text.trim { it <= ' ' })
    }

    /**
     * Check Email String is Valid or Not
     * Input:
     * 1. Email String
     * Output: True / False
     */
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(target: CharSequence): Boolean {
        return target.length in 6..15
    }

    /*fun isValidPhoneNumber(target: CharSequence): Boolean {
        return target.length < 5 && target.length > 11 && Patterns.PHONE.matcher(target).matches()
    }*/

    fun isValidPhoneNumber(target: CharSequence): Boolean {
        return target.length >= 5 && Patterns.PHONE.matcher(target).matches()
    }

    fun isNumberStartWithZero(target: CharSequence): Boolean {
        return !target.startsWith("0")
    }

    /*fun isValidPhoneNumber(target: CharSequence): Boolean {
        return target.length == 9 && Patterns.PHONE.matcher(target).matches()
    }*/

    /**
     * Check UserName String is Valid or Not
     * Input:
     * 1. String
     * Output: True / False
     */
    fun isValidUserName(userName: String): Boolean {
        return USERNAME_PATTERN.matcher(userName).matches()
    }


    fun isEmailValid(email: CharSequence?): Boolean {
        return !(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun isSignInImputValid(value: CharSequence?): Boolean {
        return value.isNullOrEmpty().not()
                && (EMAIL.matcher(value).matches()
                || USER_NAME.matcher(value).matches()
                || USER_LOOKUP.matcher(value).matches())
    }
}
