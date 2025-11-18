package com.kognitivist.trainingtracker.data.network_client

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class Retrofit() {
	private val baseUrl = "https://sr111.05.testing.place/"

	private val retrofit: Retrofit = Retrofit
		.Builder()
		.baseUrl(baseUrl)
		.client(OkHttpClient.Builder().build())
		.addConverterFactory(GsonConverterFactory.create())
		.build()

	val apiTimers: ApiTimers = retrofit.create(ApiTimers::class.java)
}