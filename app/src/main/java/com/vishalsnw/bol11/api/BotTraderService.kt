package com.vishalsnw.bol11.api

import android.util.Log
import com.vishalsnw.bol11.model.Movie
import kotlinx.coroutines.*
import kotlin.random.Random

class BotTraderService(private val onPriceUpdate: (String, Double) -> Unit) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null

    fun startSimulation(movies: List<Movie>) {
        job?.cancel()
        job = scope.launch {
            while (isActive) {
                delay(3000) // Bots trade every 3 seconds
                val randomMovie = movies.randomOrNull() ?: continue
                
                // Simulate bot action
                val changePercent = Random.nextDouble(-0.02, 0.03) // Slight upward bias
                val newPrice = randomMovie.currentPrice * (1 + changePercent)
                
                Log.d("BotTrader", "Bot traded ${randomMovie.name}. New Price: $newPrice")
                onPriceUpdate(randomMovie.id, newPrice)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}
