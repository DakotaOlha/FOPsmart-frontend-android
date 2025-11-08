package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ReportPreviewRequest(

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("endDate")
    val endDate: String? = null
)
