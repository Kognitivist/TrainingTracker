package com.kognitivist.trainingtracker.data.network_client

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiTimers {

    @GET("interval-timers/{id}")
		@Headers("App-Token: secret")
    fun getTimersById(@Path("id") id: Int): Call<Boolean>

}

