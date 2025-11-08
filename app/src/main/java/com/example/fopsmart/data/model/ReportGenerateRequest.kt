package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ReportGenerateRequest(
    @SerializedName("reportType")
     val type: String,

    @SerializedName("dateFrom")
     val startDate: String? = null,

    @SerializedName("dateTo")
     val endDate: String? = null
)
