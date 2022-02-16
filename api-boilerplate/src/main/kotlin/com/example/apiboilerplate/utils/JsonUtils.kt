package com.example.apiboilerplate.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser


object JsonUtils {

    fun toJson(obj: Any): String {
        return Gson().toJson(obj)
    }

    fun simplify(json: String?): String? {
        val gson = GsonBuilder().create()
        val el: JsonElement = JsonParser().parse(json)
        return gson.toJson(el)
    }

    fun beautify(json: String?): String? {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val el: JsonElement = JsonParser().parse(json)
        return gson.toJson(el)
    }

}