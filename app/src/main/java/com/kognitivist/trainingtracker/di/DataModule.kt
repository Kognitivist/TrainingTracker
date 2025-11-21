package com.kognitivist.trainingtracker.di

import android.content.Context
import com.kognitivist.trainingtracker.data.network_client.ApiTimers
import com.kognitivist.trainingtracker.data.network_client.Retrofit
import com.kognitivist.trainingtracker.data.pref_manager.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

	@Provides
	@Singleton
	fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
		return PreferencesManager(context)
	}
}