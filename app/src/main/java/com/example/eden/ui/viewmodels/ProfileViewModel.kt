package com.example.eden.ui.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eden.Eden
import com.example.eden.database.AppRepository
import com.example.eden.entities.ImageUri
import com.example.eden.enums.Countries
import kotlinx.coroutines.launch
import java.util.Date

class ProfileViewModel(
    private val repository: AppRepository,

    application: Eden
): AndroidViewModel(application) {

    val _counter = repository.getImgFileCounter()
    var counter = -1
    var user = repository.getUser(application.userId)
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

    fun updateProfileAndImgUri(firstName: String, lastName: String, country: Countries, contactNo: String, email: String, dob: Date,
                      isCustomImage: Boolean, imageUri: String){
        val temp = user.value!!.copy(firstName = firstName, lastName = lastName, email = email, mobileNo = contactNo, dob = dob,
            country = country, isCustomImage = isCustomImage, profileImageUri = imageUri)
        viewModelScope.launch {
            repository.upsertImgUri(ImageUri(0, imageUri))
            repository.upsertUser(temp)
        }
    }
}