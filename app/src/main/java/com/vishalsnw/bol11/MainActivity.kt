package com.vishalsnw.bol11

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.vishalsnw.bol11.databinding.ActivityMainBinding
import com.vishalsnw.bol11.ui.MarketFragment
import com.vishalsnw.bol11.ui.PortfolioFragment
import com.vishalsnw.bol11.ui.LeaderboardFragment
import com.vishalsnw.bol11.ui.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        if (savedInstanceState == null) {
            replaceFragment(MarketFragment())
        }
    }

    private fun setupNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_market -> replaceFragment(MarketFragment())
                R.id.nav_portfolio -> replaceFragment(PortfolioFragment())
                R.id.nav_leaderboard -> replaceFragment(LeaderboardFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
