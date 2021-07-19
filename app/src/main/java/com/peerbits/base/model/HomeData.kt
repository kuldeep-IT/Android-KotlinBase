package com.peerbits.base.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeData {
    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("caption")
    @Expose
    var caption: String? = null
}