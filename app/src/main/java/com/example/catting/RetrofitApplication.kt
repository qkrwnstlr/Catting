package com.example.catting

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RetrofitApplication {
    @POST("/getUser")
    fun getUserInfo(@Body uid: Uid): Call<UserInfo>

    @POST("/addUser")
    fun addUserInfo(@Body userInfo: UserInfo): Call<UserProfile>

    @POST("/updateUser")
    fun updateUserInfo(@Body userInfo: UserInfo): Call<UserProfile>

    @POST("/getCat")
    fun getCatInfo(@Body relation: Relation): Call<CatInfo>

    @POST("/addCat")
    fun addCatInfo(@Body catInfo: CatInfo): Call<CatProfile>

    @POST("/updateCat")
    fun updateCatInfo(@Body catInfo: CatInfo): Call<CatProfile>

    @POST("/deleteCat")
    fun deleteCatInfo(@Body relation: Relation): Call<Relation>

    @POST("/message")
    fun sendMessage(@Body chattingLog: ChattingLog): Call<ChattingLog>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000"
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