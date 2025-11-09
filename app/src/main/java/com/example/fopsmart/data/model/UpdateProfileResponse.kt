package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserProfile
)
