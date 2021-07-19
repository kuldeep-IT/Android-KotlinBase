package com.peerbits.base.utils

import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.peerbits.base.R
import java.io.File

/**
 * Created by ak on 07/07/18.
 */

object BindingUtils {

    @BindingAdapter("imageUrl", "isCircle", requireAll = false)
    @JvmStatic
    fun setImageUrl(
        imageView: ImageView,
        url: String?,
        circle: Boolean
    ) {
        val context = imageView.context
        val options = RequestOptions().apply {
            if (circle) {
                circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.color.colorGray)
                    .error(R.color.colorGray)
            } else {
                diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.color.colorGray)
                    .error(R.color.colorGray)
                    .signature(ObjectKey(System.currentTimeMillis().toString()))
            }
        }
        if (!TextUtils.isEmpty(url) && url!!.startsWith("http")) {
                Glide.with(context).load(url).apply(options).into(imageView)
        }
    }

    @BindingAdapter("imageUriString", "isCircle", requireAll = false)
    @JvmStatic
    fun setImageUri(imageView: ImageView, uriString: String?, circle: Boolean) {
        if (uriString != null) {
            var uri = /*Uri.parse(*/Uri.fromFile(File(uriString))
            val context = imageView.context
            val options = RequestOptions().apply {
                if (circle) {
                    circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.color.colorGray)
                        .error(R.color.colorGray)
                } else {
                        diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.color.colorGray)
                        .error(R.color.colorGray)
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                }
            }
            if (!TextUtils.isEmpty(uri.toString()) && uri.toString()!!.startsWith("http"))
                Glide.with(context).load(uri.toString()).apply(options).into(imageView)
            else if (!TextUtils.isEmpty(uri.toString()))
                Glide.with(context).load(uri).apply(options).into(imageView)
        }
    }
}// This class is not publicly instantiable
