package com.example.tempestapp

import retrofit2.Call
import retrofit2.http.GET


interface Endpoint {
    @GET("posts")
    fun getPosts() : Call<List<Posts>>
}