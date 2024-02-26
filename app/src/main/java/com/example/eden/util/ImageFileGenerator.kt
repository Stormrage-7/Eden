package com.example.eden.util

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri

object ImageFileGenerator {
    fun generate(uri: Uri, contentResolver: ContentResolver){
        contentResolver.openInputStream(uri).use { inputStream ->

            val bitmap = BitmapFactory.decodeStream(inputStream)

        }
    }
}