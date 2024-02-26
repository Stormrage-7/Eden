package com.example.eden.util

import android.net.Uri

sealed class FileGenerationResponse{
    class Success(uri: Uri) : FileGenerationResponse()
    object Error : FileGenerationResponse()
}
