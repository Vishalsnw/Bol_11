package com.vishalsnw.bol11.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishalsnw.bol11.databinding.FragmentLeaderboardBinding
import com.vishalsnw.bol11.model.Bot
import com.vishalsnw.bol11.util.GameStorage

class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: GameStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = GameStorage(requireContext())
        setupUI()
    }

    private fun setupUI() {
        val bots = storage.loadFromFile("bots.json", Array<Bot>::class.java)?.toList() 
            ?: createInitialBots()
        
        // In a real app, we'd have a LeaderboardAdapter here
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(context)
        // binding.rvLeaderboard.adapter = LeaderboardAdapter(bots)
    }

    private fun createInitialBots(): List<Bot> {
        val initialBots = List(10) { i ->
            Bot("bot_$i", "AI Trader $i", 10000.0 + (i * 500), 75.0 + i)
        }
        storage.saveToFile("bots.json", initialBots.toTypedArray())
        return initialBots
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
