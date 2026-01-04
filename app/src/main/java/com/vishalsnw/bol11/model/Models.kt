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

data class Portfolio(
    val userId: String,
    val holdings: MutableMap<String, Int> = mutableMapOf() // movieId to quantity
)

data class Transaction(
    val id: String,
    val movieId: String,
    val quantity: Int,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isBuy: Boolean
)
