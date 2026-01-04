package com.vishalsnw.bol11

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vishalsnw.bol11.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        showDisclaimer()
    }

    private fun setupUI() {
        binding.tvTitle.text = "Movie Stock Market"
        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        // Initial setup for the fantasy trading game UI
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
