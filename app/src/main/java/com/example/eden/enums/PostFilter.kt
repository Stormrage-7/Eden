package com.example.eden.enums

import com.example.eden.R

enum class PostFilter(val text: String, val imgSrc: Int) {
    TOP("TOP", R.drawable.ic_top_24),
    HOT("HOT", R.drawable.ic_hot_24),
    OLDEST("OLD", R.drawable.ic_old_24)
}