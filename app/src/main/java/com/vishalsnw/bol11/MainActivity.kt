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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MovieAdapter
    private val scraper = MovieDataScraper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadData()
        showDisclaimer()
    }

    private fun setupUI() {
        binding.tvTitle.text = "Movie Stock Market"
        adapter = MovieAdapter(emptyList())
        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        binding.rvMovies.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Loading trending movies...")
                val trendingMovies = scraper.getTrendingMovies()
                Log.d("MainActivity", "Found ${trendingMovies.size} movies")
                
                if (trendingMovies.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No movies found. Check connection.", Toast.LENGTH_LONG).show()
                }

                val movies = trendingMovies.map { 
                    Log.d("MainActivity", "Scraping $it")
                    scraper.scrapeMovieDetails(it) 
                }
                adapter.updateData(movies)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error loading data", e)
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
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
