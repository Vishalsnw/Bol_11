package com.vishalsnw.bol11.api

import com.vishalsnw.bol11.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MovieDataScraper {
    private val client = OkHttpClient()
    private val apiKey = "f18ef590e896a536910f0141ec02e660cb23744b672ebdbbf5adb35db9d2a43b"

    suspend fun getTrendingMovies(): List<String> = withContext(Dispatchers.IO) {
        try {
            val calendar = java.util.Calendar.getInstance()
            val month = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.ENGLISH)
            val year = calendar.get(java.util.Calendar.YEAR)
            val query = "latest Bollywood movies released in $month $year"
            
            val request = Request.Builder()
                .url("https://serpapi.com/search.json?q=${URLEncoder.encode(query, "UTF-8")}&engine=google&api_key=$apiKey")
                .build()

            client.newCall(request).execute().use { response ->
                val jsonData = response.body?.string() ?: throw Exception("Empty response")
                val jsonObject = JSONObject(jsonData)
                val titles = mutableSetOf<String>()

                // Try organic results
                val organicResults = jsonObject.optJSONArray("organic_results")
                organicResults?.let {
                    for (i in 0 until it.length()) {
                        val result = it.getJSONObject(i)
                        val title = result.optString("title")
                        if (title.isNotEmpty() && !title.contains("Wikipedia", true)) {
                            titles.add(title.split("-")[0].split("|")[0].trim())
                        }
                    }
                }

                // Try knowledge graph / movies carousel
                val knowledgeGraph = jsonObject.optJSONObject("knowledge_graph")
                knowledgeGraph?.optJSONArray("movies")?.let {
                    for (i in 0 until it.length()) {
                        titles.add(it.getJSONObject(i).getString("name"))
                    }
                }

                if (titles.isEmpty()) throw Exception("No titles found")
                titles.toList().take(12)
            }
        } catch (e: Exception) {
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
