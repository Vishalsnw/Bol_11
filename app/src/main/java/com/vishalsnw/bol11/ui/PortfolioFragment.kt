package com.vishalsnw.bol11.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishalsnw.bol11.MovieAdapter
import com.vishalsnw.bol11.databinding.FragmentPortfolioBinding
import com.vishalsnw.bol11.model.Movie
import com.vishalsnw.bol11.model.UserState
import com.vishalsnw.bol11.util.GameStorage

class PortfolioFragment : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: GameStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = GameStorage(requireContext())
        setupUI()
    }

    private fun setupUI() {
        val userState = storage.loadFromFile("user_state.json", UserState::class.java) ?: UserState()
        val allMovies = storage.loadFromFile("movies.json", Array<Movie>::class.java)?.toList() ?: emptyList()
        val portfolioMovies = allMovies.filter { userState.holdings.containsKey(it.id) && userState.holdings[it.id]!! > 0 }

        val adapter = MovieAdapter(portfolioMovies) { movie ->
            sellStock(movie, userState)
        }
        binding.rvPortfolio.layoutManager = LinearLayoutManager(context)
        binding.rvPortfolio.adapter = adapter
    }

    private fun sellStock(movie: Movie, userState: UserState) {
        val quantity = userState.holdings[movie.id] ?: 0
        if (quantity > 0) {
            userState.coins += movie.currentPrice
            userState.holdings[movie.id] = quantity - 1
            storage.saveToFile("user_state.json", userState)
            setupUI()
            Toast.makeText(context, "Sold ${movie.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
