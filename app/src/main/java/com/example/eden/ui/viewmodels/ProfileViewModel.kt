package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.eden.Eden
import com.example.eden.database.AppRepository

class ProfileViewModel(
    repository: AppRepository,
    application: Eden
): AndroidViewModel(application) {

    var user = repository.getUser()
    init {
        Log.i("Profile", "ProfileViewModel Initialized!")
    }

}