package com.example.appstory.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.appstory.ListStoryItem
import com.example.appstory.data.UserPreference
import com.example.appstory.networking.Api
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val pref: UserPreference, private val api: Api) :
    PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE
            val token: String = pref.getUser().first().token

            if (token.isNotEmpty()) {
                val response = api.getUserStories("Bearer $token", page, params.loadSize)
                val story = response.body()?.listStory
                if (response.isSuccessful) {
                    Log.d("Story Paging Source", "Load: ${response.body()}")
                    LoadResult.Page(
                        data = story ?: emptyList(),
                        prevKey = if (page == INITIAL_PAGE) null else page - 1,
                        nextKey = if (story.isNullOrEmpty()) null else page + 1
                    )
                } else {
                    Log.d("Token", "Load Error: $token")
                    LoadResult.Error(Exception("Failed"))
                }
            } else {
                LoadResult.Error(Exception("Failed"))
            }
        } catch (exception: Exception) {
            Log.d("Exception", "Load Error: ${exception.message}")
            return LoadResult.Error(exception)
        }
    }

}