package com.peerbits.base.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import com.peerbits.base.utils.pref.PreferenceKeys.KEY_SELECTED_LANGUAGE
import com.peerbits.base.utils.pref.SessionManager
import java.util.*

object LocaleHelper {

    var sessionManager: SessionManager? = null

//    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, Locale.getDefault().language)
        return setLocale(context, lang)
    }

    fun onAttach(context: Context, defaultLanguage: String): Context {
        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().language)
    }

    fun setLocale(context: Context, language: String?): Context {
        persist(context, language)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else updateResourcesLegacy(context, language)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        sessionManager = SessionManager(context)
        return sessionManager!!.getDataByKey(KEY_SELECTED_LANGUAGE)
//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        sessionManager = SessionManager(context)
        sessionManager!!.setValueFromKey(KEY_SELECTED_LANGUAGE, language!!)

//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val editor = preferences.edit()
//
//        editor.putString(SELECTED_LANGUAGE, language)
//        editor.apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}
