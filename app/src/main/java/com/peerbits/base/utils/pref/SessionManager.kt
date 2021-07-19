package com.peerbits.base.utils.pref

import android.content.Context
import com.peerbits.base.R
import com.peerbits.base.utils.pref.PreferenceKeys.KEY_FCM_TOKEN
import com.peerbits.base.utils.pref.PreferenceKeys.KEY_IS_LOGGED_IN
import java.util.HashMap

class SessionManager(context: Context) {
    private val pref: SecurePreferences

    var fcmToken: String
        get() = getDataByKey(KEY_FCM_TOKEN)
        set(token) {
            pref.putString(KEY_FCM_TOKEN, token)
            pref.commit()
        }

    fun setFCMToken(token: String) {
        pref.putString(KEY_FCM_TOKEN, token)
        pref.commit()
    }

    fun getFCMToken(): String {
        return getDataByKey(KEY_FCM_TOKEN)
    }

    init {
        val PREF_NAME = context.resources.getString(R.string.app_name)
        pref = SecurePreferences.getInstance(context, PREF_NAME, "")
    }

    fun clearSession() {
        pref.clear()
        pref.commit()
    }

    fun storeUserData(userDetail: HashMap<String, String>) {
        for (key in userDetail.keys) {
            pref.putString(key, userDetail[key])
        }
        pref.commit()
    }

    /**
     * Getting value for key from shared Preferences
     *
     * @param key          key for which we need to get Value
     * @param defaultValue default value to be returned if key is not exits
     * @return It will return value of key if exist and defaultValue otherwise
     */
    fun getValueFromKey(key: String, defaultValue: String): String {
        return if (pref.containsKey(key)) {
            pref.getString(key, defaultValue)
        } else {
            defaultValue
        }
    }

    /**
     * Getting value for key from shared Preferences
     *
     * @param key          key for which we need to get Value
     * @param defaultValue default value to be returned if key is not exits
     * @return It will return value of key if exist and defaultValue otherwise
     */
    fun getIntFromKey(key: String, defaultValue: Int): Int {
        return if (pref.containsKey(key)) {
            pref.getInt(key, defaultValue)
        } else {
            defaultValue
        }
    }

    /*fun setUser(userInfo: UserModel?) {
        if (userInfo != null) {
            val json = Gson().toJson(userInfo)
            pref.putString(PreferenceKeys.KEY_USER_INFO, json)
            pref.commit()
        } else {
            pref.putString(PreferenceKeys.KEY_USER_INFO, "")
        }
    }

    fun getUser(): UserModel? {
        val json: String = getDataByKey(PreferenceKeys.KEY_USER_INFO)
        return Gson().fromJson<UserModel>(json, UserModel::class.java)
    }*/

    /*fun setToken(token: TokenModel?) {
        val json = Gson().toJson(token)
        pref.putString(PreferenceKeys.KEY_TOKEN, json)
        pref.commit()
    }

    fun getToken(): TokenModel? {
        val json: String = getDataByKey(PreferenceKeys.KEY_TOKEN)
        return if (json.isNullOrEmpty()) null else Gson().fromJson<TokenModel>(
            json,
            TokenModel::class.java
        )
    }*/

    fun setIsLoggedIn(isLoggedIn: Boolean) {
        pref.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        pref.commit()
    }

    fun getIsLoggedIn(): Boolean {
        return getFlagFromKey(KEY_IS_LOGGED_IN)
    }

    /**
     * Setting value for key from shared Preferences
     *
     * @param key   key for which we need to get Value
     * @param value value for the key
     */
    fun setValueFromKey(key: String, value: String) {
        pref.putString(key, value)
    }

    /**
     * Setting value for key from shared Preferences
     *
     * @param key   key for which we need to get Value
     * @param value value for the key
     */
    fun setFlagFromKey(key: String, value: Boolean) {
        pref.putBoolean(key, value)
    }

    /**
     * To get Flag from sharedPreferences
     *
     * @param key key of flag to get
     * @return flag value for key if exist. false if not key not exist.
     */
    fun getFlagFromKey(key: String): Boolean {
        return pref.containsKey(key) && pref.getBoolean(key, false)
    }

    fun getDataByKey(Key: String): String {
        return getValueFromKey(Key, "")
    }

    companion object {
        var sessionManager: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            if (sessionManager == null) {
                sessionManager = SessionManager(context)
            }
            return sessionManager!!
        }
    }
}
