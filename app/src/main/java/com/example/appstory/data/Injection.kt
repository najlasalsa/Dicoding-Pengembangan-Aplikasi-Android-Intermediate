package com.example.appstory.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.appstory.networking.ApiConfig
import com.example.appstory.paging.StoryRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val api = ApiConfig.getApi()
        val pref = UserPreference.getInstance(context.dataStore)
        return StoryRepository(pref, api)
    }
}