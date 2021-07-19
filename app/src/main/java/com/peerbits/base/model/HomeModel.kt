package com.peerbits.base.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class HomeModel {
    @SerializedName("HomeData")
    @Expose
    var homeData: ArrayList<HomeData>? = null
}