package com.example.appstory.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.appstory.ListStoryItem
import com.example.appstory.data.UserPreference
import com.example.appstory.networking.Api

class StoryRepository(private val pref: UserPreference, private val api: Api) {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(pref, api)
            }
        ).liveData
    }
}