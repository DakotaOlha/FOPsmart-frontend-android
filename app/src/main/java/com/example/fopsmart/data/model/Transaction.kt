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

    @SerializedName("mcc")
    val mcc: Int? = null,

    @SerializedName("category")
    val category: String? = null,

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

    fun getCategoryNameUkrainian(): String {
        return com.example.fopsmart.utils.CategoryMapper.getUkrainianCategoryName(mcc)
    }

    fun getCategoryIcon(): Int {
        return com.example.fopsmart.utils.CategoryMapper.getCategoryIcon(mcc)
    }

    enum class TransactionType{
        INCOME,
        EXPENSE
    }
}