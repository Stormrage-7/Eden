package com.example.eden

import android.app.Application
import com.example.eden.database.AppDatabase
import com.example.eden.database.AppRepository
import com.example.eden.database.EdenDao
import timber.log.Timber
import kotlin.properties.Delegates

class Eden: Application() {
    lateinit var edenDao: EdenDao
    lateinit var repository: AppRepository
    var userId = 1
    override fun onCreate() {
        super.onCreate()
//        Timber.plant(Timber.DebugTree())
        edenDao = AppDatabase.getDatabase(this).edenDao()
        repository = AppRepository(edenDao)
    }
}