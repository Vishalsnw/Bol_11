package com.vishalsnw.bol11.model

data class Movie(
    val id: String,
    val name: String,
    val currentPrice: Double,
    val openingDay: String = "N/A",
    val weekendTotal: String = "N/A",
    val verdict: String = "Pending",
    val priceHistory: List<Double> = emptyList()
)

data class Trader(
    val id: String,
    val name: String,
    val type: TraderType,
    val coins: Double = 10000.0
)

enum class TraderType {
    USER, AI_TRADER, MARKET_BOT, STUDIO_BOT
}
