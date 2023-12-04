package com.example.eden

import android.app.Application
import timber.log.Timber
import kotlin.properties.Delegates

class Eden: Application() {
    lateinit var edenDao: EdenDao
    lateinit var repository: AppRepository
    var postId by Delegates.notNull<Int>()
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        edenDao = AppDatabase.getDatabase(this).edenDao()
        repository = AppRepository(edenDao)
        postId = 0
    }
}