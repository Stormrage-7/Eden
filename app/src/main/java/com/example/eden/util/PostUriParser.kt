package com.example.eden.util

object PostUriParser {
    fun parse(uri: String): Pair<Int, Int>{
        val uriElements = uri.split('/')
        return Pair(uriElements[3].toInt(), uriElements[4].toInt())
    }
}