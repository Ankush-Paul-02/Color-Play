package com.example.di

import android.content.Context
import com.example.audio.SoundPlayer
import com.example.data.AppDatabase
import com.example.data.AppRepository

/**
 * Dependency Injection container providing application-scoped singleton instances.
 */
interface AppContainer {
    val repository: AppRepository
    val soundPlayer: SoundPlayer
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val repository: AppRepository by lazy {
        AppRepository(database.appDao())
    }

    override val soundPlayer: SoundPlayer by lazy {
        SoundPlayer(context)
    }
}
