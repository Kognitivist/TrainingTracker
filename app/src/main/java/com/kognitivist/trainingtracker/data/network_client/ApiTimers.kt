package com.kognitivist.trainingtracker.data.network_client

import com.kognitivist.trainingtracker.data.models.TimerResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiTimers {

    @GET("interval-timers/{id}")
		@Headers(
			"App-Token: secret",
			"Authorization: Bearer pdhO16atBIXogpPzaLDjDcl5Gpmbz9Mdl1mjhrhWZBuOgNCgxDlk7mMIbFcEc7mj"
		)
    fun getTimerById(@Path("id") id: Int): Call<TimerResponse>

}

