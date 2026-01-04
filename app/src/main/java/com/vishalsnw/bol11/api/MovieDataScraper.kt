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
            // Optimized query for fresh Jan 2026 data
            val url = "https://www.google.com/search?q=new+bollywood+movies+releasing+this+week+box+office"
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                .timeout(8000)
                .get()
            
            val titles = doc.select("h3").map { it.text() }
            val filtered = titles.filter { 
                it.contains("Box Office", ignoreCase = true) || 
                it.contains("Collection", ignoreCase = true) ||
                it.contains("Review", ignoreCase = true)
            }.map { 
                it.split("|", "-", ":", "–").first()
                    .replace("Box Office", "", ignoreCase = true)
                    .replace("Collection", "", ignoreCase = true)
                    .trim() 
            }.filter { it.length > 2 && it.split(" ").size <= 5 }
            
            if (filtered.size < 3) {
                // Hardcoded Jan 2026 actual/projected releases to ensure quality
                listOf("Sky Force", "Game Changer", "Thougheelu", "Chhaava", "Fateh", "Raid 2", "Vrushabha")
            } else {
                filtered.distinct().take(10)
            }
        } catch (e: Exception) {
            listOf("Sky Force", "Game Changer", "Thougheelu", "Chhaava", "Fateh")
        }
    }

    suspend fun scrapeMovieDetails(movieName: String): Movie = withContext(Dispatchers.IO) {
        val query = URLEncoder.encode("$movieName box office collection", "UTF-8")
        val url = "$searchUrl$query"
        
        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                .timeout(10000)
                .get()

            val bodyText = doc.text()
            
            val openingDay = extractNumber(bodyText, "opening day") ?: 
                           extractNumber(bodyText, "Day 1") ?: "Coming Soon"
            val weekendTotal = extractNumber(bodyText, "weekend") ?: 
                             extractNumber(bodyText, "total") ?: 
                             extractNumber(bodyText, "collection") ?: "0.0 Cr"
            
            val verdict = when {
                bodyText.contains("All Time Blockbuster", ignoreCase = true) -> "ATB"
                bodyText.contains("Blockbuster", ignoreCase = true) -> "Blockbuster"
                bodyText.contains("Hit", ignoreCase = true) -> "Hit"
                bodyText.contains("Flop", ignoreCase = true) -> "Flop"
                bodyText.contains("releasing", ignoreCase = true) -> "Upcoming"
                else -> "Live"
            }

            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                currentPrice = weekendTotal.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 100.0,
                openingDay = openingDay,
                weekendTotal = weekendTotal,
                verdict = verdict
            )
        } catch (e: Exception) {
            Movie(
                id = movieName.hashCode().toString(),
                name = movieName,
                currentPrice = 100.0,
                verdict = "Trading"
            )
        }
    }

    private fun extractNumber(text: String, keyword: String): String? {
        val regex = "$keyword.*?(\\d+\\.?\\d*\\s*(?:Crore|Million|Cr|M))".toRegex(RegexOption.IGNORE_CASE)
        return regex.find(text)?.groupValues?.get(1)
    }
}
