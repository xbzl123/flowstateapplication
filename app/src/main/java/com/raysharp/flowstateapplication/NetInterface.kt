package com.raysharp.flowstateapplication

import retrofit2.http.GET

/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * NetInterface
 * @author longyanghe
 * @date 2022-09-27
 */
data class ResponseBean(val result: String,val data:Data)
data class Data(val AccessToken:String)


interface NetInterface {
    @GET("googlecodelabs/kotlin-coroutines/master/advanced-coroutines-codelab/sunflower/src/main/assets/plants.json")
    suspend fun getToken() : List<Plant>
}