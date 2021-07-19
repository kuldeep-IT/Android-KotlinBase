package com.peerbits.base.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.peerbits.base.R
import java.io.File


class GlideLoader public constructor(private val context: Context) {

    private val simpleOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    private val withoutCache = RequestOptions()
        .centerCrop()
        .placeholder(R.color.colorGray)
        .error(R.color.colorGray)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)

    private val circleOptions = RequestOptions()
        .circleCrop()
        // .error(R.drawable.ic_placeholder)
        // .placeholder(R.drawable.ic_placeholder)
//        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    private val roundedCorners = RequestOptions()
        .placeholder(R.color.colorGray)
        .error(R.color.colorGray)
        .transforms(CenterCrop(), RoundedCorners(30))
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    private val circleOptionsError = RequestOptions()
        .circleCrop()
        .placeholder(R.color.colorGray)
        .error(R.color.colorGray)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    private val circleOptionsErrorNoPlaceHolder = RequestOptions()
        .circleCrop()
        .error(R.color.colorGray)
        .diskCacheStrategy(DiskCacheStrategy.ALL)


    /**
     *@param url - URL for downloading the image
     * @param view - image view in which the downloaded image will be displayed without any format
     */
    fun loadImageSimple(url: String, view: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(simpleOptions)
            .into(view)
    }

    /**
     *@param url - URL for downloading the image
     * @param view - image view in which the downloaded image will be displayed without any format
     */
    fun loadImageNoCache(url: Uri, view: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(withoutCache)
            .into(view)
    }

    /**
     * @param uri - URI of the image to be loaded
     * @param view - image view in which the downloaded image will be displayed without any format
     */
    fun loadImageSimple(uri: Uri?, view: ImageView) {
        Glide.with(context)
            .load(uri)
            .apply(simpleOptions)
            .into(view)
    }

    /**
     * @param uri - URI of the image to be loaded
     * @param view - image view in which the downloaded image will be displayed without any format
     */
    fun loadImageSimpleCircle(uri: Uri?, view: ImageView) {
        Glide.with(context)
            .load(uri)
            .apply(circleOptions)
            .into(view)
    }

    /**
     *@param url - URL for downloading the image
     * @param view - image view in which the downloaded image will be displayed in a circle
     */
    fun loadImageCircle(url: String?, view: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(circleOptions)
            .into(view)
    }

    fun loadImageCircleWithErrorImg(url: String?, view: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(circleOptionsError)
            .into(view)
    }

    /**
     *@param file - path of file that is to be loaded
     * @param view - image view in which the downloaded image will be displayed in a circle
     */

    fun loadImageFromFile(file: File, view: ImageView) {
        Glide.with(context)
            .load(file)
            .apply(simpleOptions)
            .into(view)
    }

    fun loadRoundCornerImageFromFile(file: File, view: ImageView) {
        Glide.with(context)
            .load(file)
            .apply(roundedCorners)
            .into(view)
    }

    fun loadRoundCornerImageUrl(url: String?, view: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(roundedCorners)
            .into(view)
    }

    fun loadCircleImageFromFile(file: File, view: ImageView) {
        Glide.with(context)
            .load(file)
            .apply(circleOptions)
            .into(view)
    }

    /**
     * Gets the GlideLoader singleton
     *
     * @return the GlideLoader singleton
     */
    companion object {
        private var loader: GlideLoader? = null
        fun getInstance(context: Context): GlideLoader {
            loader = GlideLoader(context)
            /*if (loader == null) {
                loader = GlideLoader(context)
            }*/
            return loader as GlideLoader
        }
    }

    /*fun loadImageForZoom(url: String, view: SubsamplingScaleImageView) {
        val requestBuilder = Glide.with(context).asBitmap()
        requestBuilder.load(url)
                .apply(simpleOptions)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        view.setImage(ImageSource.bitmap(resource))
                    }
                })
    }*/

}