package com.example.eden

import android.app.Application
import timber.log.Timber

class Eden: Application() {
    lateinit var viewModelFactory: ViewModelFactory
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val databaseDao = AppDatabase.getDatabase(this).edenDao()
        val repository = AppRepository(databaseDao)
        viewModelFactory = ViewModelFactory(repository)
    }
}