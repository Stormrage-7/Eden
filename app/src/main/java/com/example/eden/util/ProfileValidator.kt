package com.example.eden.util

import java.util.regex.Matcher
import java.util.regex.Pattern


object ProfileValidator {
    fun validateMobileNo(mobile: String): Boolean{
        val patterns = ("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$")

        val pattern: Pattern = Pattern.compile(patterns)
        val matcher: Matcher = pattern.matcher(mobile)
        return matcher.matches()
    }

    fun validateName(name: String): Boolean{
        val pattern = "^[a-zA-Z]{1,25}$".toRegex()
        return name.matches(pattern)
    }
}