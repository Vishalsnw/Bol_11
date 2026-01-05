package com.vishalsnw.bol11.api

import android.content.Context
import com.vishalsnw.bol11.model.Movie
import com.vishalsnw.bol11.util.GameStorage
import kotlinx.coroutines.*
import kotlin.random.Random

class BotTraderService(private val context: Context, private val onPriceUpdate: () -> Unit) {
    private val storage = GameStorage(context)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null

    fun startSimulation() {
        job?.cancel()
        job = scope.launch {
            while (isActive) {
                delay(5000)
                val movies = storage.loadFromFile("movies.json", Array<Movie>::class.java)?.toMutableList() ?: continue
                
                var changed = false
                movies.forEach { movie ->
                    if (movie.status != "Market Closed") {
                        val changePercent = Random.nextDouble(-0.01, 0.015)
                        movie.currentPrice *= (1 + changePercent)
                        changed = true
                    }
                }

                if (changed) {
                    storage.saveToFile("movies.json", movies.toTypedArray())
                    withContext(Dispatchers.Main) {
                        onPriceUpdate()
                    }
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}
