package com.kognitivist.trainingtracker.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kognitivist.trainingtracker.app.App
import com.kognitivist.trainingtracker.data.models.toTimerResponse
import com.kognitivist.trainingtracker.data.pref_manager.PreferencesManager
import com.kognitivist.trainingtracker.service.IntervalService
import com.kognitivist.trainingtracker.ui.screens.start.StartScreen
import com.kognitivist.trainingtracker.ui.screens.start.StartViewModel
import com.kognitivist.trainingtracker.ui.screens.training.TrainingScreen
import com.kognitivist.trainingtracker.ui.screens.training.TrainingViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavHost() {
	val navigator = rememberNavController()
	val startVM = hiltViewModel<StartViewModel>()
	val trainingVM = hiltViewModel<TrainingViewModel>()
	val startScreenState = startVM.startScreenState.collectAsStateWithLifecycle().value
	val trainingScreenState = trainingVM.trainingScreenState.collectAsStateWithLifecycle().value
	val context = LocalContext.current
	val preferencesManager = remember { PreferencesManager(context) }
	val scope = rememberCoroutineScope()
	val isRunningService = App.isRunningService.collectAsStateWithLifecycle().value
	val currentTimerString = preferencesManager.currentTimerFlow.collectAsStateWithLifecycle("").value

	LaunchedEffect(currentTimerString) {
		if (currentTimerString.isNotEmpty()) {
			val timer = currentTimerString.toTimerResponse()
			trainingVM.changeState(
				trainingScreenState.copy(
					intervals = timer.timer?.intervals?.map { it.time?:0 } ?: listOf(),
					sum = timer.timer?.total_time ?: 0
				)
			)
			navigator.navigate(Screens.TRAINING.name){launchSingleTop = true}
		}
	}
	fun startService(){
		val intent = Intent(context, IntervalService::class.java).apply {
			putIntegerArrayListExtra(
				IntervalService.EXTRA_INTERVALS,
				ArrayList(trainingScreenState.intervals)
			)
		}
		ContextCompat.startForegroundService(context, intent)
	}
	fun stopService(){
		val stopIntent = Intent(context, IntervalService::class.java).apply {
			action = IntervalService.ACTION_STOP
		}
		context.startService(stopIntent)
	}

	NavHost(
		navController = navigator,
		startDestination = Screens.START.name
	){
		composable(
			route = Screens.START.name
		){
			StartScreen(
				state = startScreenState,
				loadIntervals = {
					startScreenState.id.toIntOrNull()?.let {id->
						startVM.loadTimer(id, isMock = startScreenState.isMock){ timer ->
							navigator.navigate(Screens.TRAINING.name){launchSingleTop = true}
							trainingVM.changeState(
								trainingScreenState.copy(
									intervals = timer.timer?.intervals?.map { it.time?:0 } ?: listOf(),
									sum = timer.timer?.total_time ?: 0
								)
							)
						}
					}
				},
				changeState = {startVM.changeState(it)}
			)
		}
		composable(
			route = Screens.TRAINING.name
		){
			TrainingScreen(
				state = trainingScreenState,
				changeState = {trainingVM.changeState(it)},
				onStart = {
					scope.launch {
						preferencesManager.setListGeoPoint("")
					}
					startService()
									},
				onStop = {
					stopService()
				},
				onBack = {
					if (isRunningService){
						stopService()
					}else{
						scope.launch {
							preferencesManager.setCurrentTimer("")
							preferencesManager.setListGeoPoint("")
						}
						navigator.popBackStack()
					}
				}
			)
		}
	}
}


enum class Screens(){
	START, TRAINING
}