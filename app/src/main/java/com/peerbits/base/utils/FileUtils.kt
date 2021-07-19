package com.peerbits.base.utils

import android.R.attr.path
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * File utils
 * Created by Mauro on 2017-09-11.
 */
object FileUtils {
    private val TAG = FileUtils::class.java.simpleName
    private val SHARED_IMAGES_DIR = "shared_images"
    private val SHARED_IMAGE_FILENAME = "shared_map.png"

    fun readBytes(file: File): ByteArray? {
        val size = file.length().toInt()
        val bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(bytes, 0, bytes.size)
            buf.close()
            return bytes
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return null
    }

    fun saveFile(context: Context, filename: String, payload: ByteArray): Uri? {
        var outUri: Uri? = null
        var outputStream: FileOutputStream? = null
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            outputStream.write(payload)
            val outFile = context.getFileStreamPath(filename)
            outUri = Uri.fromFile(outFile)
        } catch (e: Exception) {
            Timber.e(TAG, "Error saving file to internal storage")
        } finally {
            if (outputStream != null) {
                outputStream.close()
            }
        }
        return outUri
    }

    fun saveFileForSharing(context: Context, bitmap: Bitmap): Boolean {
        var outputStream: FileOutputStream? = null
        try {
            val cachePath = File(context.filesDir, SHARED_IMAGES_DIR)
            cachePath.mkdirs() // don't forget to make the directory
            val file = File(cachePath, SHARED_IMAGE_FILENAME)
            outputStream = FileOutputStream(file.path) // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return true
        } catch (e: Exception) {
            Timber.e(TAG, "Error saving file to internal storage")
            return false
        } finally {
            if (outputStream != null) {
                outputStream.close()
            }
        }
    }

    fun getSavedFileForSharingUri(context: Context): Uri? {
        val imagePath = File(context.filesDir, SHARED_IMAGES_DIR)
        val file = File(imagePath, SHARED_IMAGE_FILENAME)
        return FileProvider.getUriForFile(context, context.packageName + ".FileProvider", file)
    }
}