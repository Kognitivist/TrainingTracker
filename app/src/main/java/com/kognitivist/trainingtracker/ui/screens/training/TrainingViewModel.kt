package com.kognitivist.trainingtracker.ui.screens.training

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(): ViewModel() {

	private val _trainingScreenState = MutableStateFlow(TrainingScreenState())
	val trainingScreenState: StateFlow<TrainingScreenState> = _trainingScreenState

	fun changeState(newState: TrainingScreenState){
		_trainingScreenState.update { newState }
	}
}