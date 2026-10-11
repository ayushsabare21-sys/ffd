package com.example.data.model

enum class ExpenseCategory(val displayName: String, val iconName: String) {
    FOOD("Food & Meals", "restaurant"),
    ROOM_RENT("Room Rent", "home"),
    LIGHT_BILL("Electricity / Light Bill", "bolt"),
    MOBILE_RECHARGE("Mobile Recharge", "phone_android"),
    TRANSPORT("Transport / Commute", "directions_bus"),
    HEALTH("Health & Medical", "local_hospital"),
    WORK_PORTER("Company / Porter", "local_shipping"),
    OTHER("Other Spends", "payments");

    companion object {
        fun fromString(name: String): ExpenseCategory {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}
