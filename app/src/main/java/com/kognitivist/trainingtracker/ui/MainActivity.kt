package com.kognitivist.trainingtracker.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.kognitivist.trainingtracker.ui.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val permissions = arrayOf(
		Manifest.permission.POST_NOTIFICATIONS,
		Manifest.permission.ACCESS_COARSE_LOCATION,
		Manifest.permission.ACCESS_FINE_LOCATION,
		Manifest.permission.FOREGROUND_SERVICE,
		Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK,
	)

	private val requestPermissionsLauncher =
		registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->

			val notificationsGranted = result[Manifest.permission.POST_NOTIFICATIONS] == true
			val coarseGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
			val fineGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
			val service = result[Manifest.permission.FOREGROUND_SERVICE] == true
			val serviceMP = result[Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK] == true

			Log.d("PermissionsTag","$notificationsGranted $coarseGranted $fineGranted $service $serviceMP")

			if (notificationsGranted && coarseGranted && fineGranted) {

			} else {

			}
		}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		requestPermissionsLauncher.launch(permissions)
		setContent {
			AppNavHost()
		}
	}
}
