package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.entities.Comment
import kotlinx.coroutines.launch
class CommentsViewModel(private val repository: AppRepository,
                        application: Eden
): AndroidViewModel(application) {

    fun upsertComment(comment: Comment) {
        viewModelScope.launch {
            repository.upsertComment(comment)
        }
    }
}