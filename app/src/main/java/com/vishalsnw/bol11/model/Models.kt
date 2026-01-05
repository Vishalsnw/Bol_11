package com.vishalsnw.bol11.model

data class Movie(
    val id: String,
    val name: String,
    val releaseYear: Int,
    var currentPrice: Double,
    val status: String, // "Market Open", "Trading Live", "Market Closed"
    val releaseDate: Long // Timestamp
)

data class UserState(
    var coins: Double = 10000.0,
    val holdings: MutableMap<String, Int> = mutableMapOf(), // MovieId to Quantity
    val buyPrices: MutableMap<String, Double> = mutableMapOf() // MovieId to Avg Buy Price
)

data class Bot(
    val id: String,
    val name: String,
    val coins: Double,
    val accuracy: Double,
    val isAI: Boolean = true
)
