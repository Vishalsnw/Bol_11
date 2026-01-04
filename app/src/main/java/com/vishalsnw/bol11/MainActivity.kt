package com.vishalsnw.bol11

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vishalsnw.bol11.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.vishalsnw.bol11.api.MovieDataScraper
import com.vishalsnw.bol11.model.Movie
import kotlinx.coroutines.launch

import android.util.Log
import android.widget.Toast

import com.vishalsnw.bol11.api.BotTraderService

import android.content.Context
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MovieAdapter
    private val scraper = MovieDataScraper()
    private lateinit var botService: BotTraderService
    private var userCoins = 10000.0
    private var currentMovies = mutableListOf<Movie>()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefs = getSharedPreferences("Bol11Prefs", Context.MODE_PRIVATE)
        userCoins = prefs.getFloat("user_coins", 10000f).toDouble()

        botService = BotTraderService { id, newPrice ->
            runOnUiThread {
                val index = currentMovies.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentMovies[index] = currentMovies[index].copy(currentPrice = newPrice)
                    adapter.updateData(currentMovies)
                }
            }
        }

        setupUI()
        loadData()
        showDisclaimer()
    }

    private fun setupUI() {
        binding.tvTitle.text = "Movie Stock Market"
        updateWalletUI()
        adapter = MovieAdapter(emptyList()) { movie ->
            buyStock(movie)
        }
        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        binding.rvMovies.adapter = adapter
    }

    private fun updateWalletUI() {
        binding.tvWallet.text = "Coins: ${String.format("%.0f", userCoins)}"
        prefs.edit().putFloat("user_coins", userCoins.toFloat()).apply()
    }

    private fun buyStock(movie: Movie) {
        if (userCoins >= movie.currentPrice) {
            userCoins -= movie.currentPrice
            updateWalletUI()
            
            // Save holding
            val holdings = prefs.getInt("holding_${movie.id}", 0)
            prefs.edit().putInt("holding_${movie.id}", holdings + 1).apply()
            
            Toast.makeText(this, "Purchased ${movie.name} for ${String.format("%.2f", movie.currentPrice)}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Loading trending movies...")
                val trendingMovies = scraper.getTrendingMovies()
                val movies = trendingMovies.map { scraper.scrapeMovieDetails(it) }
                currentMovies.clear()
                currentMovies.addAll(movies)
                adapter.updateData(currentMovies)
                botService.startSimulation(currentMovies)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error loading data", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        botService.stop()
    }

    private fun showDisclaimer() {
        val disclaimer = """
            This app is a skill-based movie prediction game.
            All data is collected from public sources and used only for gameplay simulation.
            AI traders are part of the game design.
            Virtual coins have no real-world value.
        """.trimIndent()
        
        Snackbar.make(binding.root, disclaimer, Snackbar.LENGTH_INDEFINITE)
            .setAction("OK") {}
            .show()
    }
}
