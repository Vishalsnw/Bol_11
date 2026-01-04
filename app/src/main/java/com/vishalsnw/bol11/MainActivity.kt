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
            val sampleMovies = listOf("Pushpa 2", "Singham Again", "Bhool Bhulaiyaa 3")
            val movies = sampleMovies.map { scraper.scrapeMovieDetails(it) }
            adapter.updateData(movies)
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
