package com.peerbits.base

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.peerbits.base.network.NetworkCall
import com.peerbits.base.network.listeners.DefaultActionPerformer
import com.peerbits.base.utils.AppConstants
import com.peerbits.base.utils.GlideLoader
import com.peerbits.base.utils.LocaleHelper
import com.peerbits.base.utils.pref.PreferenceKeys
import com.peerbits.base.utils.pref.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.HashMap

class AppClass : MultiDexApplication() {

    var sessionManager: SessionManager? = null

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        sessionManager = SessionManager(applicationContext)
        printHashKey(this)

        val userModule = module {
            single { SessionManager(get()) }
            single { GlideLoader(get()) }
        }
        startKoin {
            androidContext(this@AppClass)
            modules(userModule)
        }

//        NetworkCall(applicationContext).setCustomBaseURL(Constants.BASE_URL)

        NetworkCall.setActionPerformer(object : DefaultActionPerformer {
            override fun onActionPerform(
                headers: HashMap<String, String>,
                params: HashMap<String, String>
            ) {
            }
        })
    }

    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager
                .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Timber.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Timber.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Timber.e(TAG, "printHashKey()", e)
        }
    }

    // endregion
    companion object {
        var TAG = this.javaClass.simpleName
    }

    /**
     * For dynamic language change
     * @param base - default language
     */
    override fun attachBaseContext(base: Context) {
//        var language = "en"
        var language = "en"
        when (sessionManager?.getDataByKey(PreferenceKeys.KEY_SELECTED_LANGUAGE)?.toLowerCase()) {
            AppConstants.ENGLISH_LANGUAGE -> {
                language = AppConstants.ENGLISH_LANGUAGE
            }
            AppConstants.ARABIC_LANGUAGE -> {
                language = AppConstants.ARABIC_LANGUAGE
            }
        }
        super.attachBaseContext(LocaleHelper.onAttach(base, language))
    }
}