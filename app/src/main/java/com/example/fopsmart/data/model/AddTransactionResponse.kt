package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class AddTransactionResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("transaction")
    val transaction: TransactionCreated
)

data class TransactionCreated(
    @SerializedName("id")
    val id: Int,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("comment")
    val comment: String?,

    @SerializedName("date")
    val date: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("mcc")
    val mcc: Int?,

    @SerializedName("account")
    val account: CreatedTransactionAccount,

    @SerializedName("createdAt")
    val createdAt: String
)

data class CreatedTransactionAccount(
    @SerializedName("id")
    val id: Int
)