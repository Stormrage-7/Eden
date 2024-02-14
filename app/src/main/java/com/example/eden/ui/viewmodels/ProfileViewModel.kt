package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.entities.User
import com.example.eden.enums.Countries
import kotlinx.coroutines.launch
import java.util.Date

class ProfileViewModel(
    val repository: AppRepository,
    application: Eden
): AndroidViewModel(application) {

    var user = repository.getUser()
    init {
        Log.i("Profile", "ProfileViewModel Initialized!")
    }

    fun updateProfile(firstName: String, lastName: String, country: Countries, contactNo: String, email: String, dob: Date,
                      isCustomImage: Boolean, imageUri: String){
        val temp = user.value!!.copy(firstName = firstName, lastName = lastName, email = email, mobileNo = contactNo, dob = dob,
            country = country, isCustomImage = isCustomImage, profileImageUri = imageUri)
        viewModelScope.launch {
            repository.upsertUser(temp)
        }
    }
}