package com.example.appstory.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.appstory.data.UserModel
import com.example.appstory.data.UserPreference
import com.example.appstory.networking.ApiConfig
import com.example.appstory.responses.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewStoryViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoad = MutableLiveData<Boolean>()
    val isLoad: LiveData<Boolean> = _isLoad

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _addResp = MutableLiveData<AddStoryResponse>()
    val addResp: LiveData<AddStoryResponse> = _addResp

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun addStories(token: String, file: MultipartBody.Part, description: RequestBody) {
        _isLoad.value = true
        val client = ApiConfig.getApi().addStories("Bearer $token", file, description)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                _isLoad.value = false
                if (response.isSuccessful && response.body() != null) {
                    _addResp.value = response.body()
                    _message.value = response.message()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _message.value = t.message
            }

        })
    }

    companion object {
        private const val TAG = "NewStoryViewModel"
    }
}