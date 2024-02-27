package com.example.eden.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File

object ImageGenerator {
    fun generate (context: Context, uri: String, counter: Int) : FileGenerationResponse{
        if (!UriValidator.validate(context, uri)) return FileGenerationResponse.Error

        val albumName = "Eden_Media"
        val rootPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/$albumName/"
        Log.i("root", "$rootPath")
        val rootFolder = File(rootPath)
        if (!rootFolder.exists()) rootFolder.mkdirs()

        //TODO INSERT DB COUNT
        val fileName = "img${counter}"
        val file = File (rootPath + fileName)
        if (!file.exists()) file.createNewFile()

        context.contentResolver.openInputStream(Uri.parse(uri)).use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        val outputUri = Uri.fromFile(file)
        Log.i("File", "Output URI = $outputUri")
        return FileGenerationResponse.Success(outputUri)
//        application.counter++
    }
}