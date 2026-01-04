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
            val url = "https://www.google.com/search?q=latest+bollywood+movies+box+office+collection+today"
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get()
            
            // Extract movie names from search result titles or snippets
            val titles = doc.select("h3").map { it.text() }
            titles.filter { it.contains("Box Office", ignoreCase = true) }
                .map { it.split("|", "-", ":").first().trim() }
                .distinct()
                .take(10)
        } catch (e: Exception) {
            listOf("Pushpa 2", "Singham Again", "Chhaava", "Game Changer", "Sky Force")
        }
    }

    suspend fun scrapeMovieDetails(movieName: String): Movie = withContext(Dispatchers.IO) {
        val query = URLEncoder.encode("$movieName box office collection", "UTF-8")
        val url = "$searchUrl$query"
        
        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get()

            // Extracting numbers from search result snippets
            val bodyText = doc.text()
            
            val openingDay = extractNumber(bodyText, "opening day") ?: "0.0"
            val weekendTotal = extractNumber(bodyText, "weekend") ?: "0.0"
            val verdict = if (bodyText.contains("Hit", ignoreCase = true)) "Hit" 
                          else if (bodyText.contains("Flop", ignoreCase = true)) "Flop" 
                          else "Average"

            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                currentPrice = weekendTotal.toDoubleOrNull() ?: 100.0,
                openingDay = openingDay,
                weekendTotal = weekendTotal,
                verdict = verdict
            )
        } catch (e: Exception) {
            // Smooth errors: fallback to a base estimation if scraping fails
            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                currentPrice = 100.0,
                verdict = "Average"
            )
        }
    }

    private fun extractNumber(text: String, keyword: String): String? {
        val regex = "$keyword.*?(\\d+\\.?\\d*\\s*(?:Crore|Million|Cr|M))".toRegex(RegexOption.IGNORE_CASE)
        return regex.find(text)?.groupValues?.get(1)
    }
}
