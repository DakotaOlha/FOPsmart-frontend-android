package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ReportTypesResponse(
    @SerializedName("types")
    val types: List<ReportType>
)
