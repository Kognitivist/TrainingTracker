package com.kognitivist.trainingtracker.ui.screens.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun StartScreen(
	state: StartScreenState,
	loadIntervals:()->Unit,
	changeState:(StartScreenState)-> Unit
) {

	Column(
		modifier = Modifier.fillMaxSize().imePadding().padding(16.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		OutlinedTextField(
			value = state.id,
			onValueChange = { changeState(state.copy(id = it)) },
			modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
		)
		Spacer(Modifier.height(16.dp))
		Button(
			onClick = loadIntervals,
			enabled = !state.isLoading,
			modifier = Modifier.height(48.dp)
		) {
			Box(
				contentAlignment = Alignment.Center
			){
				Text("Загрузить")
				if (state.isLoading){
					CircularProgressIndicator(modifier = Modifier.size(32.dp))
				}
			}
		}
		Spacer(Modifier.height(16.dp))
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				checked = state.isMock,
				onCheckedChange = {
					changeState(state.copy(isMock = it))
				}
			)
			Spacer(Modifier.width(16.dp))
			Text("Mock")
		}
	}
}

@Stable
data class StartScreenState(
	val isLoading: Boolean = false,
	val id: String = "68",
	val isMock: Boolean = false
)