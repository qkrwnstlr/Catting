package com.example.catting

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitApplication {
    @FormUrlEncoded
    @POST("/user/{uid}}")
    fun getUserInfo(@Field("uid") uid: String): Call<UserInfo>

    @POST("/user/{uid}}")
    fun getCatsInfo(@Field("uid") uid: String): Call<ArrayList<CatInfo>>

    @POST("/user")
    fun sendUserInfo(@Body userInfo: UserInfo): Call<UserInfo>

    @POST("/user")
    fun sendCatsInfo(@Body catsInfo: ArrayList<CatInfo>): Call<UserInfo>

    @POST("/")
    fun sendMessage(@Body messageLog: MessageLog): Call<MessageLog>

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