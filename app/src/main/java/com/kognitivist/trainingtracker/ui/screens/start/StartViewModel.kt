package com.kognitivist.trainingtracker.ui.screens.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kognitivist.trainingtracker.data.models.Interval
import com.kognitivist.trainingtracker.data.models.Timer
import com.kognitivist.trainingtracker.data.models.TimerResponse
import com.kognitivist.trainingtracker.data.models.toJson
import com.kognitivist.trainingtracker.data.network_client.ApiTimers
import com.kognitivist.trainingtracker.data.pref_manager.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
	private val apiTimers: ApiTimers,
	private val preferencesManager: PreferencesManager
) : ViewModel() {

	private val _startScreenState = MutableStateFlow(StartScreenState())
	val startScreenState: StateFlow<StartScreenState> = _startScreenState

	fun loadTimer(id: Int, isMock: Boolean = false, onSuccess: (TimerResponse) -> Unit) {
		_startScreenState.update { it.copy(isLoading = true) }
		if (isMock){
			viewModelScope.launch { preferencesManager.setCurrentTimer(mockTimer.toJson()) }
			_startScreenState.update { it.copy(isLoading = false) }
			onSuccess(mockTimer)
			return
		}
		apiTimers.getTimerById(id).enqueue(
			object : Callback<TimerResponse> {
				override fun onResponse(
					call: Call<TimerResponse?>,
					response: Response<TimerResponse?>
				) {
					if (response.code() == 200) {
						response.body()?.let {
							viewModelScope.launch { preferencesManager.setCurrentTimer(it.toJson()) }
							onSuccess(it)
						}
					}
					_startScreenState.update { it.copy(isLoading = false) }
				}

				override fun onFailure(call: Call<TimerResponse?>, t: Throwable) {
					_startScreenState.update { it.copy(isLoading = false) }
				}
			}
		)
	}

	fun changeState(newState: StartScreenState) {
		_startScreenState.update { newState }
	}
}

val mockTimer = TimerResponse(
	timer = Timer(
		intervals = listOf(Interval(time = 5),Interval(time = 6),Interval(time = 7),Interval(time = 5),),
		total_time = 23
	)
)