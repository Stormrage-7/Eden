package com.example.eden.util

object PostUriValidator {
    private val pattern = "https://www.eden.com/[0-9]+/[0-9]+".toRegex()
    fun validate(uri: String?) = uri?.matches(pattern) ?: false
}