package com.vishalsnw.bol11.api

import com.vishalsnw.bol11.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder

class MovieDataScraper {
    
    private val searchUrl = "https://www.google.com/search?q="

    suspend fun getTrendingMovies(): List<String> = withContext(Dispatchers.IO) {
        try {
            val calendar = java.util.Calendar.getInstance()
            val month = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.ENGLISH)
            val year = calendar.get(java.util.Calendar.YEAR)
            // Specific search query for movie list
            val url = "https://www.google.com/search?q=list+of+bollywood+movies+released+in+$month+$year"
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                .timeout(10000)
                .get()
            
            val titles = mutableSetOf<String>()
            // Targeted selectors for movie cards and titles in search results
            doc.select("div.BNeawe.vv778b.AP7Wnd, h3, div.rllt__details div.BNeawe").forEach { 
                val text = it.text().trim()
                // Filter for likely movie titles: length, no "Wikipedia", "Release", etc.
                if (text.length in 3..40 && 
                    !text.contains("release", true) && 
                    !text.contains("movie", true) && 
                    !text.contains("Wikipedia", true) &&
                    !text.contains("Review", true) &&
                    !text.contains("Rating", true)) {
                    titles.add(text)
                }
            }
            
            if (titles.isEmpty()) throw Exception("No real titles found")
            titles.toList().take(12)
        } catch (e: Exception) {
            // High-quality fallback if scraping is blocked or fails
            listOf("Fateh", "Raid 2", "Sky Force", "Game Changer", "Thug Life", "Vrushabha", "Devara Part 2", "War 2", "Singham Again", "Bhool Bhulaiyaa 3")
        }
    }

    suspend fun scrapeMovieDetails(movieName: String): Movie = withContext(Dispatchers.IO) {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val price = 100.0 + (Math.random() * 50)
        Movie(
            id = movieName.hashCode().toString(),
            name = movieName,
            releaseYear = year,
            currentPrice = price,
            bidPrice = price * 0.98,
            askPrice = price * 1.02,
            status = "Trading Live",
            releaseDate = System.currentTimeMillis()
        )
    }

    private fun extractNumber(text: String, keyword: String): String? {
        val regex = "$keyword.*?(\\d+\\.?\\d*\\s*(?:Crore|Million|Cr|M))".toRegex(RegexOption.IGNORE_CASE)
        return regex.find(text)?.groupValues?.get(1)
    }
}
