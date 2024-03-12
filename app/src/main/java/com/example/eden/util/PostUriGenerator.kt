package com.example.eden.util

object PostUriGenerator {
    fun generate(postId: Int, communityId: Int): String {
        return "https://www.eden.com/$postId/$communityId"
    }
}