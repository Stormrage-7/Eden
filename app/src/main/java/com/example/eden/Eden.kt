package com.example.eden

import android.app.Application
import timber.log.Timber

class Eden: Application() {
    lateinit var edenDao: EdenDao
    lateinit var repository: AppRepository
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        edenDao = AppDatabase.getDatabase(this).edenDao()
        repository = AppRepository(edenDao)
    }
}