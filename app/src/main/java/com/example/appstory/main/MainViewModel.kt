package com.example.appstory.main

import android.util.Log
import androidx.lifecycle.*
import com.example.appstory.GetStoriesResponse
import com.example.appstory.data.UserModel
import com.example.appstory.data.UserPreference
import com.example.appstory.networking.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoad = MutableLiveData<Boolean>()
    val isLoad: LiveData<Boolean> = _isLoad

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _listStory = MutableLiveData<GetStoriesResponse>()
    val listStory: LiveData<GetStoriesResponse> = _listStory

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getStoriesLocation(token: String) {
        viewModelScope.launch {
            val client = ApiConfig.getApi().getStoriesLocation("Bearer $token")

            client.enqueue(object : Callback<GetStoriesResponse> {
                override fun onResponse(
                    call: Call<GetStoriesResponse>,
                    response: Response<GetStoriesResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        _listStory.value = response.body()
                    } else {
                        Log.e(
                            TAG,
                            "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }

                override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }

    fun userLogout() {
        viewModelScope.launch {
            pref.userLogout()
        }
    }

    companion object {
        private const val TAG = " MainViewModel"
    }
}