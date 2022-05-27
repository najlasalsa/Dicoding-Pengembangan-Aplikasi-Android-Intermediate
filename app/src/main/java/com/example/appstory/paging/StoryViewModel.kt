package com.example.appstory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.appstory.data.Injection
import com.example.appstory.paging.StoryRepository

class StoryViewModel(storyRep: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<ListStoryItem>> =
        storyRep.getStories().cachedIn(viewModelScope)
}

class StoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}