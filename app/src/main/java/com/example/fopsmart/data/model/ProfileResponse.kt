package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("fopGroup") val fopGroup: Int?,
    @SerializedName("taxSystem") val taxSystem: String?,
    @SerializedName("createdAt") val createdAt: String
)
