package com.vishalsnw.bol11.util

import android.content.Context
import com.google.gson.Gson
import java.io.File

class GameStorage(private val context: Context) {
    private val gson = Gson()

    fun <T> saveToFile(fileName: String, data: T) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    fun <T> loadFromFile(fileName: String, type: Class<T>): T? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return null
        return try {
            gson.fromJson(file.readText(), type)
        } catch (e: Exception) {
            null
        }
    }
}
