package com.example.fopsmart.utils

import com.example.fopsmart.R

object CategoryMapper {

    private val mccToUkrainianName = mapOf(
        5411 to "Продуктові магазини",
        5812 to "Ресторани",
        5814 to "Фастфуд",
        5541 to "АЗС",
        4121 to "Таксі",
        5311 to "Універмаги",
        5651 to "Одяг",
        5999 to "Роздріб",
        5912 to "Аптеки",
        8011 to "Лікарі",
        4900 to "Комунальні",
        4814 to "Зв'язок",
        6011 to "Готівка",
        6012 to "Банки",
        5968 to "Підписки",
        5734 to "ПЗ",
        0 to "Інше"
    )

    private val parentCategoryToUkrainian = mapOf(
        "Food" to "Їжа",
        "Transport" to "Транспорт",
        "Shopping" to "Покупки",
        "Health" to "Здоров'я",
        "Bills" to "Рахунки",
        "Cash" to "Готівка",
        "Finance" to "Фінанси",
        "Digital" to "Цифрове",
        "Other" to "Інше"
    )

    private val categoryToIcon = mapOf(
        // Їжа
        5411 to R.drawable.ic_category_grocery,
        5812 to R.drawable.ic_category_restaurant,
        5814 to R.drawable.ic_category_fastfood,

        // Транспорт
        5541 to R.drawable.ic_category_fuel,
        4121 to R.drawable.ic_category_taxi,

        // Покупки
        5311 to R.drawable.ic_category_shopping,
        5651 to R.drawable.ic_category_clothes,
        5999 to R.drawable.ic_category_retail,

        // Здоров'я
        5912 to R.drawable.ic_category_pharmacy,
        8011 to R.drawable.ic_category_doctor,

        // Рахунки
        4900 to R.drawable.ic_category_utilities,
        4814 to R.drawable.ic_category_telecom,

        // Фінанси
        6011 to R.drawable.ic_category_cash,
        6012 to R.drawable.ic_category_bank,

        // Цифрове
        5968 to R.drawable.ic_category_subscription,
        5734 to R.drawable.ic_category_software,

        // Інше
        0 to R.drawable.ic_category_other
    )

    fun getUkrainianCategoryName(mccCode: Any?): String {
        val code = when (mccCode) {
            is String -> mccCode.toIntOrNull() ?: 0
            is Int -> mccCode
            is Long -> mccCode.toInt()
            else -> 0
        }

        return mccToUkrainianName[code] ?: "Інше"
    }

    fun getUkrainianParentCategory(parentCategory: String?): String {
        return parentCategoryToUkrainian[parentCategory] ?: "Інше"
    }

    fun getCategoryIcon(mccCode: Any?): Int {
        val code = when (mccCode) {
            is String -> mccCode.toIntOrNull() ?: 0
            is Int -> mccCode
            is Long -> mccCode.toInt()
            else -> 0
        }

        return categoryToIcon[code] ?: R.drawable.ic_category_other
    }

    fun getCategoryIconByName(categoryName: String?): Int {
        return when (categoryName?.lowercase()) {
            "продуктові магазини", "grocery stores", "їжа", "food" -> R.drawable.ic_category_grocery
            "ресторани", "restaurants" -> R.drawable.ic_category_restaurant
            "фастфуд", "fast food" -> R.drawable.ic_category_fastfood
            "азс", "gas stations", "паливо" -> R.drawable.ic_category_fuel
            "таксі", "taxi", "транспорт", "transport" -> R.drawable.ic_category_taxi
            "універмаги", "department stores", "покупки", "shopping" -> R.drawable.ic_category_shopping
            "одяг", "clothing" -> R.drawable.ic_category_clothes
            "роздріб", "retail" -> R.drawable.ic_category_retail
            "аптеки", "pharmacies", "здоров'я", "health" -> R.drawable.ic_category_pharmacy
            "лікарі", "doctors" -> R.drawable.ic_category_doctor
            "комунальні", "utilities", "рахунки", "bills" -> R.drawable.ic_category_utilities
            "зв'язок", "telecom" -> R.drawable.ic_category_telecom
            "готівка", "cash" -> R.drawable.ic_category_cash
            "банки", "banks", "фінанси", "finance" -> R.drawable.ic_category_bank
            "підписки", "subscriptions", "цифрове", "digital" -> R.drawable.ic_category_subscription
            "пз", "software" -> R.drawable.ic_category_software
            else -> R.drawable.ic_category_other
        }
    }

    fun getCategoryColor(mccCode: Any?): String {
        val code = when (mccCode) {
            is String -> mccCode.toIntOrNull() ?: 0
            is Int -> mccCode
            is Long -> mccCode.toInt()
            else -> 0
        }

        return when (code) {
            5411, 5812, 5814 -> "#4CAF50" // Їжа - зелений
            5541, 4121 -> "#2196F3" // Транспорт - синій
            5311, 5651, 5999 -> "#E91E63" // Покупки - рожевий
            5912, 8011 -> "#F44336" // Здоров'я - червоний
            4900, 4814 -> "#FF9800" // Рахунки - помаранчевий
            6011, 6012 -> "#9C27B0" // Фінанси - фіолетовий
            5968, 5734 -> "#00BCD4" // Цифрове - бірюзовий
            else -> "#9E9E9E" // Інше - сірий
        }
    }

    fun isFopIncome(mccCode: Any?): Boolean {
        return false
    }

    fun getAllCategories(): List<Pair<Int, String>> {
        return mccToUkrainianName.toList().sortedBy { it.second }
    }


    fun getAllParentCategories(): List<Pair<String, String>> {
        return parentCategoryToUkrainian.toList()
    }
}