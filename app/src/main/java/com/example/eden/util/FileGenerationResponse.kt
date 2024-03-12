package com.example.eden.util

import android.net.Uri

sealed class FileGenerationResponse{
    class Success(val uri: Uri) : FileGenerationResponse()
    object Error : FileGenerationResponse()
}
