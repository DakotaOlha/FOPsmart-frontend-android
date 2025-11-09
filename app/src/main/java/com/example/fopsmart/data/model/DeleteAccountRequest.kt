package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class DeleteAccountRequest(
    @SerializedName("password") val password: String,
    @SerializedName("confirmation") val confirmation: String
)
