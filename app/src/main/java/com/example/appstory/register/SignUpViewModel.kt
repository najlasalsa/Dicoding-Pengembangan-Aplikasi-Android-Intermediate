package com.example.appstory.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appstory.networking.ApiConfig
import com.example.appstory.responses.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    private val _isLoad = MutableLiveData<Boolean>()
    val isLoad: LiveData<Boolean> = _isLoad

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _regResp = MutableLiveData<RegisterResponse>()
    val regResp: LiveData<RegisterResponse> = _regResp

    fun postRegister(name: String, email: String, password: String) {
        _isLoad.value = true
        val client = ApiConfig.getApi().postRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoad.value = false
                if (response.isSuccessful && response.body() != null) {
                    _regResp.value = response.body()
                    _message.value = response.body()?.message
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _message.value = response.body()?.message.toString()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoad.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _message.value = t.message
            }
        })
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}