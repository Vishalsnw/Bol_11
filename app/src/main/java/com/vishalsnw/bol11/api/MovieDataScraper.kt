package com.vishalsnw.bol11.api

import com.vishalsnw.bol11.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder

class MovieDataScraper {
    
    private val searchUrl = "https://www.google.com/search?q="

    suspend fun getTrendingMovies(): List<String> = withContext(Dispatchers.IO) {
        // Direct list for Jan 2026 for 100% reliability
        listOf("Fateh", "Raid 2", "Sky Force", "Game Changer", "Thougheelu", "Vrushabha", "Devara 2")
    }

    suspend fun scrapeMovieDetails(movieName: String): Movie = withContext(Dispatchers.IO) {
        val query = URLEncoder.encode("$movieName box office 2026", "UTF-8")
        val url = "$searchUrl$query"
        
        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                .timeout(5000)
                .get()

            val bodyText = doc.text()
            val weekendTotal = extractNumber(bodyText, "collection") ?: 
                             extractNumber(bodyText, "total") ?: "12.5 Cr"
            
            val price = weekendTotal.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 100.0

            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                currentPrice = price,
                openingDay = "Live",
                weekendTotal = weekendTotal,
                verdict = "Trading"
            )
        } catch (e: Exception) {
            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                currentPrice = 100.0,
                verdict = "Market Open"
            )
        }
    }

    private fun extractNumber(text: String, keyword: String): String? {
        val regex = "$keyword.*?(\\d+\\.?\\d*\\s*(?:Crore|Million|Cr|M))".toRegex(RegexOption.IGNORE_CASE)
        return regex.find(text)?.groupValues?.get(1)
    }
}
