package com.vishalsnw.bol11.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.vishalsnw.bol11.MovieAdapter
import com.vishalsnw.bol11.api.MovieDataScraper
import com.vishalsnw.bol11.databinding.FragmentMarketBinding
import com.vishalsnw.bol11.model.Movie
import com.vishalsnw.bol11.model.UserState
import com.vishalsnw.bol11.util.GameStorage
import kotlinx.coroutines.launch

class MarketFragment : Fragment() {
    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: GameStorage
    private val scraper = MovieDataScraper()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = GameStorage(requireContext())
        initializeDataIfNeeded()
        setupUI()
    }

    fun refreshUI() {
        activity?.runOnUiThread {
            setupUI()
        }
    }

    private fun initializeDataIfNeeded() {
        val existingMovies = storage.loadFromFile("movies.json", Array<Movie>::class.java)
        if (existingMovies == null || existingMovies.isEmpty()) {
            lifecycleScope.launch {
                val movieNames = scraper.getTrendingMovies()
                val movieList = movieNames.map { name ->
                    scraper.scrapeMovieDetails(name)
                }
                storage.saveToFile("movies.json", movieList.toTypedArray())
                setupUI()
            }
        }
    }

    private fun setupUI() {
        val movies = storage.loadFromFile("movies.json", Array<Movie>::class.java)?.toList() ?: emptyList()
        val userState = storage.loadFromFile("user_state.json", UserState::class.java) ?: UserState()
        
        val adapter = MovieAdapter(movies) { movie ->
            buyStock(movie, userState)
        }
        binding.rvMarket.layoutManager = LinearLayoutManager(context)
        binding.rvMarket.adapter = adapter
    }

    private fun buyStock(movie: Movie, userState: UserState) {
        if (userState.coins >= movie.currentPrice) {
            userState.coins -= movie.currentPrice
            userState.holdings[movie.id] = (userState.holdings[movie.id] ?: 0) + 1
            storage.saveToFile("user_state.json", userState)
            Toast.makeText(context, "Bought ${movie.name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
