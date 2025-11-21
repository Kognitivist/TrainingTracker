package com.kognitivist.trainingtracker.data.network_client

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit() {
	private val baseUrl = "https://sr111.05.testing.place/api/v2/"

	private val retrofit: Retrofit = Retrofit
		.Builder()
		.baseUrl(baseUrl)
		.client(
			OkHttpClient.Builder()
				.addInterceptor(
					HttpLoggingInterceptor().apply {
						level = HttpLoggingInterceptor.Level.BODY
					}
				)
				.build()
		)
		.addConverterFactory(GsonConverterFactory.create())
		.build()

	val apiTimers: ApiTimers = retrofit.create(ApiTimers::class.java)
}