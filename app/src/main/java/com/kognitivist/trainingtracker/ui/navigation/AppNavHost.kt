package com.kognitivist.trainingtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kognitivist.trainingtracker.ui.screens.start.StartScreen
import com.kognitivist.trainingtracker.ui.screens.start.StartViewModel
import com.kognitivist.trainingtracker.ui.screens.training.TrainingScreen

@Composable
fun AppNavHost() {
	val navigator = rememberNavController()
	val startVM = hiltViewModel<StartViewModel>()
	NavHost(
		navController = navigator,
		startDestination = Screens.START.name
	){
		composable(
			route = Screens.START.name
		){
			StartScreen(
				navToTrainingScreen = {},
				loadIntervals = {
					startVM.
				}
			)
		}
		composable(
			route = Screens.TRAINING.name
		){
			TrainingScreen()
		}
	}
}

enum class Screens(){
	START, TRAINING
}