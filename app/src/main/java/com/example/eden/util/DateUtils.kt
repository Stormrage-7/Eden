package com.example.eden.util

import java.text.SimpleDateFormat
import java.util.Date

object DateUtils {
    fun toSimpleString(date: Date) : String {
        val format = SimpleDateFormat("dd/MM/yyy")
        return format.format(date)
    }
}