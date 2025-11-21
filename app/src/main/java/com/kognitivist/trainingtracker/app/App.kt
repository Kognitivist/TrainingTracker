package com.kognitivist.trainingtracker.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltAndroidApp
class App : Application(){
	companion object{
		private val _isRunningService = MutableStateFlow(false)
		val isRunningService: StateFlow<Boolean> = _isRunningService
		fun setRunningService(value: Boolean) { _isRunningService.value = value }
	}
}



