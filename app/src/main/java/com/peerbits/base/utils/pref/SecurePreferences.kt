package com.peerbits.base.utils.pref

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecurePreferences @Throws(SecurePreferencesException::class)
constructor(
    context: Context, preferenceName: String,
    secureKey: String, encryptKeys: Boolean
) {

    private val encryptKeys: Boolean
    private val writer: Cipher
    private val reader: Cipher
    private val keyWriter: Cipher
    private val preferences: SharedPreferences

    val iv: IvParameterSpec
        get() {
            val iv = ByteArray(writer.blockSize)
            System.arraycopy(
                "}{(**&%%^*(}\\({#@}!&*(#!&#%*(@*~".toByteArray(), 0,
                iv, 0, writer.blockSize
            )
            return IvParameterSpec(iv)
        }

    class SecurePreferencesException internal constructor(e: Throwable) : RuntimeException(e)

    init {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION)
            this.reader = Cipher.getInstance(TRANSFORMATION)
            this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION)

            initCiphers(secureKey)

            this.preferences = context.getSharedPreferences(
                preferenceName,
                Context.MODE_PRIVATE
            )

            this.encryptKeys = encryptKeys
        } catch (e: GeneralSecurityException) {
            throw SecurePreferencesException(e)
        } catch (e: UnsupportedEncodingException) {
            throw SecurePreferencesException(e)
        }
    }

    @Throws(
        UnsupportedEncodingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class
    )
    fun initCiphers(secureKey: String) {
        val ivSpec = iv
        val secretKey = getSecretKey(secureKey)

        writer.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        reader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        keyWriter.init(Cipher.ENCRYPT_MODE, secretKey)
    }

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
    fun getSecretKey(key: String): SecretKeySpec {
        val keyBytes = createKeyBytes(key)
        return SecretKeySpec(keyBytes, TRANSFORMATION)
    }

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
    fun createKeyBytes(key: String): ByteArray {
        val md = MessageDigest
            .getInstance(SECRET_KEY_HASH_TRANSFORMATION)
        md.reset()
        return md.digest(key.toByteArray(charset(CHARSET)))
    }

    fun putString(key: String, value: String?) {
        if (value == null) {
            preferences.edit().remove(toKey(key)).apply()
        } else {
            putValue(toKey(key), value)
        }
    }

    fun putBoolean(key: String, value: Boolean?) {
        if (value == null) {
            preferences.edit().remove(toKey(key)).apply()
        } else {
            putValue(toKey(key), java.lang.Boolean.toString(value))
        }
    }

    fun putLong(key: String, value: Long) {
        putValue(toKey(key), java.lang.Long.toString(value))
    }

    fun putInt(key: String, value: Int) {
        putValue(toKey(key), Integer.toString(value))
    }

    fun containsKey(key: String): Boolean {
        return preferences.contains(toKey(key))
    }

    fun removeValue(key: String) {
        preferences.edit().remove(toKey(key)).apply()
    }

    @Throws(SecurePreferencesException::class)
    fun getString(key: String, value: String): String {
        if (preferences.contains(toKey(key))) {
            val securedEncodedValue = preferences.getString(toKey(key), "")
            return decrypt(securedEncodedValue!!)
        }
        return value
    }

    @Throws(SecurePreferencesException::class)
    fun getLong(key: String, value: Long): Long {
        if (preferences.contains(toKey(key))) {
            val securedEncodedValue = preferences.getString(toKey(key), "")
            return java.lang.Long.parseLong(decrypt(securedEncodedValue!!))
        }
        return value
    }

    @Throws(SecurePreferencesException::class)
    fun getBoolean(key: String, value: Boolean): Boolean {
        if (preferences.contains(toKey(key))) {
            val securedEncodedValue = preferences.getString(toKey(key), "")
            return java.lang.Boolean.parseBoolean(decrypt(securedEncodedValue!!))
        }
        return value
    }

    @Throws(SecurePreferencesException::class)
    fun getInt(key: String, value: Int): Int {
        if (preferences.contains(toKey(key))) {
            val securedEncodedValue = preferences.getString(toKey(key), "")
            return Integer.parseInt(decrypt(securedEncodedValue!!))
        }
        return value
    }

    fun commit() {
        preferences.edit().apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    fun toKey(key: String): String {
        return if (encryptKeys)
            encrypt(key, keyWriter)
        else
            key
    }

    @Throws(SecurePreferencesException::class)
    fun putValue(key: String, value: String) {
        val secureValueEncoded = encrypt(value, writer)
        preferences.edit().putString(key, secureValueEncoded).apply()
    }

    @Throws(SecurePreferencesException::class)
    fun encrypt(value: String, writer: Cipher): String {
        val secureValue: ByteArray
        try {
            secureValue = convert(writer, value.toByteArray(charset(CHARSET)))
        } catch (e: UnsupportedEncodingException) {
            throw SecurePreferencesException(e)
        }

        return Base64.encodeToString(
            secureValue,
            Base64.NO_WRAP
        )
    }

    fun decrypt(securedEncodedValue: String): String {
        val securedValue = Base64
            .decode(securedEncodedValue, Base64.NO_WRAP)
        val value = convert(reader, securedValue)
        try {
            return String(value, Charset.defaultCharset())
        } catch (e: UnsupportedEncodingException) {
            throw SecurePreferencesException(e)
        }
    }

    companion object {

        private val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private val KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding"
        private val SECRET_KEY_HASH_TRANSFORMATION = "SHA-256"
        private val CHARSET = "UTF-8"
        private var instance: SecurePreferences? = null

        fun getInstance(contxt: Context, prefName: String, secureKey: String): SecurePreferences {
            if (instance == null) {
                instance = SecurePreferences(
                    contxt, prefName,
                    secureKey, true
                )
            }
            return instance!!
        }

        @Throws(SecurePreferencesException::class)
        fun convert(cipher: Cipher, bs: ByteArray): ByteArray {
            try {
                return cipher.doFinal(bs)
            } catch (e: Exception) {
                throw SecurePreferencesException(e)
            }
        }
    }
}