package com.peerbits.base.network.model

import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("amount")
    val amount: Int = 0,
    @SerializedName("request_title")
    val requestTitle: String = "",
    @SerializedName("request_date")
    val requestDate: Int = 0,
    @SerializedName("request_id")
    val requestId: String = ""
)