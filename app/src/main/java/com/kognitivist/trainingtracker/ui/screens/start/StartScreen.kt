package com.kognitivist.trainingtracker.ui.screens.start

import android.R.attr.text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun StartScreen(
	state: StartScreenState,
	navToTrainingScreen:()-> Unit,
	loadIntervals:(Int, (Boolean)-> Unit )->Unit
) {
	var textFieldValue by rememberSaveable { mutableStateOf("68") }
	var isLoading by rememberSaveable { mutableStateOf(false) }


	Column(
		modifier = Modifier.fillMaxSize().padding(16.dp),
		verticalArrangement = Arrangement.Center
	) {
		OutlinedTextField(
			value = state.id.toString(),
			onValueChange = { it.toIntOrNull()?.let { text -> textFieldValue = text } },
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(Modifier.height(16.dp))
		Button(
			onClick = {
				isLoading = true
				textFieldValue.toIntOrNull()?.let {
					loadIntervals(it){isSuccess->
						isLoading = false
						if (isSuccess) navToTrainingScreen()
					}
				}
			}
		) {
			Text("Загрузить")
		}
	}
}

@Stable
data class StartScreenState(
	val isLoading: Boolean = false,
	val id: Int = 68
)