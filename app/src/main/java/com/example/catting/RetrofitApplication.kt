package com.example.catting

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RetrofitApplication {
    @POST("/user/{uid}")
    fun getUserInfo(@Path("uid") uid: String): Call<UserInfo>

    @POST("/user")
    fun sendUserInfo(@Body userInfo: UserInfo): Call<UserInfo>

    @POST("/user")
    fun sendMessage(@Body chattingLog: ChattingLog): Call<ChattingLog>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3030"
        fun create(): RetrofitApplication{
            val gson = GsonBuilder().setLenient().create()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitApplication::class.java)
        }
    }

}