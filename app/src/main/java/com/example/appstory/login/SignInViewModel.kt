package com.example.appstory.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appstory.LoginResponse
import com.example.appstory.data.UserModel
import com.example.appstory.data.UserPreference
import com.example.appstory.networking.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoad = MutableLiveData<Boolean>()
    val isLoad: LiveData<Boolean> = _isLoad

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loginResp = MutableLiveData<LoginResponse>()
    val loginResp: LiveData<LoginResponse> = _loginResp

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun userLogin() {
        viewModelScope.launch {
            pref.userLogin()
        }
    }

    fun postLogin(email: String, password: String) {
        _isLoad.value = true
        val client = ApiConfig.getApi().postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoad.value = false
                if (response.isSuccessful && response.body() != null) {
                    _loginResp.value = response.body()
                    _message.value = response.body()?.message
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoad.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _message.value = t.message
            }

        })

    }

    companion object {
        private const val TAG = "SignInViewModel"
    }

}