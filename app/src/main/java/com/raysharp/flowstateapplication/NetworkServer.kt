package com.raysharp.flowstateapplication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * NetworkServer
 * @author longyanghe
 * @date 2022-09-27
 */
class NetworkServer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val myService = retrofit.create(NetInterface::class.java)

    suspend fun getToken(): List<Plant> = withContext(Dispatchers.Default) {
        delay(1500)
        val result = myService.getToken()
        result.shuffled()
    }
}