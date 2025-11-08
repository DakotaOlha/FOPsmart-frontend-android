package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ReportPreview(
    @SerializedName("fopOnly")
    val fopOnly: Boolean,

    @SerializedName("user")
    val user: ReportUser,

    @SerializedName("period")
    val period: ReportPeriod,

    @SerializedName("summary")
    val summary: ReportSummary,

    @SerializedName("limitStatus")
    val limitStatus: ReportLimitStatus
)

data class ReportUser(
    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("fopGroup")
    val fopGroup: Int
)

data class ReportPeriod(
    @SerializedName("from")
    val from: String,

    @SerializedName("to")
    val to: String,

    @SerializedName("days")
    val days: Int
)

data class ReportSummary(
    @SerializedName("income")
    val income: Double,

    @SerializedName("expenses")
    val expenses: Double,

    @SerializedName("netIncome")
    val netIncome: Double
)

data class ReportLimitStatus(
    @SerializedName("fopGroup")
    val fopGroup: Int,

    @SerializedName("currentIncome")
    val currentIncome: String,

    @SerializedName("limit")
    val limit: Double,

    @SerializedName("percentage")
    val percentage: Double,

    @SerializedName("remaining")
    val remaining: Double
)