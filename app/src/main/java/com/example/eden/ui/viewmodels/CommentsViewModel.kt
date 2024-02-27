package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.entities.Comment
import com.example.eden.entities.relations.ImageUri
import kotlinx.coroutines.launch
class CommentsViewModel(private val repository: AppRepository,
                        application: Eden
): AndroidViewModel(application) {

    val _counter = repository.getImgFileCounter()
    var counter = -1

    fun upsertComment(comment: Comment, imgUri: ImageUri) {
        viewModelScope.launch {
            repository.upsertImgUri(imgUri)
            repository.upsertComment(comment)
        }
    }

    fun upsertComment(comment: Comment) {
        viewModelScope.launch {
            repository.upsertComment(comment)
        }
    }
}