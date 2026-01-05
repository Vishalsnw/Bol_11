package com.vishalsnw.bol11.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishalsnw.bol11.MovieAdapter
import com.vishalsnw.bol11.databinding.FragmentMarketBinding
import com.vishalsnw.bol11.model.Movie
import com.vishalsnw.bol11.model.UserState
import com.vishalsnw.bol11.util.GameStorage

class MarketFragment : Fragment() {
    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: GameStorage

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

    private fun initializeDataIfNeeded() {
        if (storage.loadFromFile("movies.json", Array<Movie>::class.java) == null) {
            val initialMovies = arrayOf(
                Movie("1", "Fateh", 2026, 150.0, "Trading Live", System.currentTimeMillis()),
                Movie("2", "Raid 2", 2026, 120.0, "Trading Live", System.currentTimeMillis()),
                Movie("3", "Sky Force", 2026, 180.0, "Market Open", System.currentTimeMillis() + 86400000)
            )
            storage.saveToFile("movies.json", initialMovies)
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
