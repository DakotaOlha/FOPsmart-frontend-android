package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class Transaction (
    @SerializedName("id")
    val id: String,

    @SerializedName("id")
    val date: String,

    @SerializedName("id")
    val amount: Double,

    @SerializedName("id")
    val currency: String,

    @SerializedName("id")
    val category: String,

    @SerializedName("id")
    val description: String,

    @SerializedName("id")
    val type: String,

    @SerializedName("id")
    val bankAccountId: String

) {
    fun getTransactionType(): TransactionType {
        return when (type.lowercase()) {
            "income" -> TransactionType.INCOME
            "expense" -> TransactionType.EXPENSE
            else -> TransactionType.EXPENSE
        }
    }

    fun getAbsoluteAmount(): Double {
        return kotlin.math.abs(amount)
    }


    enum class TransactionType{
        INCOME,
        EXPENSE
    }
}