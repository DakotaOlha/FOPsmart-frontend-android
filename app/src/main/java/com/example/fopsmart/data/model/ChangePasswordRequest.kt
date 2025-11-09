package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)
