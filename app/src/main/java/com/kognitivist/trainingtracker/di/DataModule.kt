package com.kognitivist.trainingtracker.di

import com.kognitivist.trainingtracker.data.network_client.ApiTimers
import com.kognitivist.trainingtracker.data.network_client.Retrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataModule {
	@Provides
	@Singleton
	fun provideRetrofit(): Retrofit {
		return Retrofit()
	}

	@Provides
	@Singleton
	fun provideApiTimers(): ApiTimers {
		return Retrofit().apiTimers
	}
}