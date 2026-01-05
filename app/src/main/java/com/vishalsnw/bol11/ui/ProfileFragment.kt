package com.vishalsnw.bol11.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vishalsnw.bol11.databinding.FragmentProfileBinding
import com.vishalsnw.bol11.model.UserState
import com.vishalsnw.bol11.util.GameStorage

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: GameStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = GameStorage(requireContext())
        setupUI()
    }

    private fun setupUI() {
        val userState = storage.loadFromFile("user_state.json", UserState::class.java) ?: UserState()
        binding.tvCoins.text = "Total Coins: ${String.format("%.0f", userState.coins)}"
        binding.tvDisclaimer.text = """
            This is a skill-based movie prediction game.
            All prices and results are simulated.
            Virtual coins have no real-world value.
            AI traders are part of the game design.
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
