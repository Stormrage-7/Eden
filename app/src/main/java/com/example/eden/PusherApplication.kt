package com.example.eden

import android.app.Application
import timber.log.Timber

class PusherApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}