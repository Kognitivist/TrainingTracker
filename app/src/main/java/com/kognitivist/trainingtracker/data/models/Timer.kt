package com.kognitivist.trainingtracker.data.models

import com.google.gson.Gson

data class TimerResponse(
    val timer: Timer? = null
)

data class Timer(
	val intervals: List<Interval>? = null,
	val timer_id: Int? = null,
	val title: String? = null,
	val total_time: Int? = null
)

data class Interval(
	val time: Int? = null,
	val title: String? = null
)

fun TimerResponse.toJson(): String {
	return try {
		Gson().toJson(this)
	}catch (e: Exception){
		""
	}
}

fun String.toTimerResponse(): TimerResponse {
	return try {
		Gson().fromJson(this, TimerResponse::class.java)
	}catch (e: Exception){
		TimerResponse()
	}
}