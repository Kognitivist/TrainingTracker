package com.kognitivist.trainingtracker.ui.screens.start

import androidx.lifecycle.ViewModel
import com.kognitivist.trainingtracker.data.network_client.ApiTimers
import com.kognitivist.trainingtracker.data.network_client.Retrofit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
	private val apiTimers: ApiTimers
): ViewModel() {

	private val _startScreenState = MutableStateFlow(StartScreenState())
	val startScreenState: StateFlow<StartScreenState> = _startScreenState

	fun loadIntervals(id:Int, onSuccess:()-> Unit){
		_startScreenState.update { it.copy(isLoading = true) }
		apiTimers.getTimersById(id).enqueue(
			object:Callback<Boolean>{
				override fun onResponse(
					call: Call<Boolean?>,
					response: Response<Boolean?>
				) {
					if (response.code() == 200){ onSuccess() }
					_startScreenState.update { it.copy(isLoading = false) }
				}

				override fun onFailure(call: Call<Boolean?>, t: Throwable) {
					_startScreenState.update { it.copy(isLoading = false) }
				}
			}
		)
	}
}