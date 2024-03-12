package com.example.eden.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.InputStream


object UriValidator {
    fun validate(context: Context, uri: String?): Boolean{
        var bool = false

        if (null != uri) {
            try {
                val inputStream: InputStream = context.contentResolver.openInputStream(Uri.parse(uri))!!
                inputStream.close()
                bool = true
            } catch (e: Exception) {
                Log.w("Validator", "File corresponding to the uri does not exist $uri")
            }
        }
        return bool
    }
}