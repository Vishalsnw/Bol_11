package com.vishalsnw.bol11.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishalsnw.bol11.MovieAdapter
import com.vishalsnw.bol11.databinding.FragmentMarketBinding
import com.vishalsnw.bol11.model.Movie
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
        setupUI()
    }

    private fun setupUI() {
        val movies = storage.loadFromFile("movies.json", Array<Movie>::class.java)?.toList() ?: emptyList()
        val adapter = MovieAdapter(movies) { movie ->
            // Buy logic here
        }
        binding.rvMarket.layoutManager = LinearLayoutManager(context)
        binding.rvMarket.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
