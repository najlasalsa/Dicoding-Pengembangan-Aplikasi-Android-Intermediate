package com.example.appstory.networking

import com.example.appstory.GetStoriesResponse
import com.example.appstory.LoginResponse
import com.example.appstory.responses.AddStoryResponse
import com.example.appstory.responses.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getUserStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetStoriesResponse>

    @GET("stories")
    fun getStoriesLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Call<GetStoriesResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>
}