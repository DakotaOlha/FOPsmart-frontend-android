package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Transaction (
    @SerializedName("id")
    val id: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("bankAccountId")
    val bankAccountId: String

): Serializable {
    fun getTransactionType(): TransactionType {
        return if (amount > 0) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
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