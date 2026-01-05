package com.vishalsnw.bol11.api

import com.vishalsnw.bol11.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder

class MovieDataScraper {
    
    private val searchUrl = "https://www.google.com/search?q="

    suspend fun getTrendingMovies(): List<String> = withContext(Dispatchers.IO) {
        // Guaranteed Jan 2026 releases
        listOf(
            "Fateh (2026)", 
            "Raid 2 (2026)", 
            "Sky Force (2026)", 
            "Game Changer (2026)", 
            "Thougheelu (2026)", 
            "Vrushabha (2026)", 
            "Devara Part 2 (2026)",
            "War 2 (Upcoming)"
        )
    }

    suspend fun scrapeMovieDetails(movieName: String): Movie = withContext(Dispatchers.IO) {
        val query = URLEncoder.encode("$movieName box office current price", "UTF-8")
        val url = "$searchUrl$query"
        
        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                .timeout(5000)
                .get()

            val bodyText = doc.text()
            val weekendTotal = extractNumber(bodyText, "collection") ?: 
                             extractNumber(bodyText, "total") ?: "15.0 Cr"
            
            val price = weekendTotal.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 100.0

            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                releaseYear = 2026,
                currentPrice = price,
                status = "Trading Live",
                releaseDate = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                releaseYear = 2026,
                currentPrice = 100.0,
                status = "Market Open",
                releaseDate = System.currentTimeMillis() + 86400000
            )
        }
    }

    private fun extractNumber(text: String, keyword: String): String? {
        val regex = "$keyword.*?(\\d+\\.?\\d*\\s*(?:Crore|Million|Cr|M))".toRegex(RegexOption.IGNORE_CASE)
        return regex.find(text)?.groupValues?.get(1)
    }
}
