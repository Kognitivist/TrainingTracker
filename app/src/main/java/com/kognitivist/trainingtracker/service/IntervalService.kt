package com.kognitivist.trainingtracker.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kognitivist.trainingtracker.R
import com.kognitivist.trainingtracker.app.App
import com.kognitivist.trainingtracker.data.pref_manager.PreferencesManager
import com.kognitivist.trainingtracker.ui.screens.training.toJson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@AndroidEntryPoint
class IntervalService : Service() {

	@Inject
	lateinit var preferencesManager: PreferencesManager

	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	private val listGeoPoint = mutableListOf<GeoPoint>()

	override fun onBind(intent: Intent?): IBinder? = null

	override fun onCreate() {
		super.onCreate()
		App.setRunningService(true)
		createNotificationChannel()
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		if (intent?.action == ACTION_STOP) {
			scope.launch {
				saveTrack()
				stopForeground(STOP_FOREGROUND_REMOVE)
				stopSelf()
			}
			return START_NOT_STICKY
		}

		val intervals = intent?.getIntegerArrayListExtra(EXTRA_INTERVALS) ?: return START_NOT_STICKY
		startForeground(NOTIFICATION_ID, buildNotification("Старт"))
		scope.launch {
			runIntervals(intervals)
			saveTrack()
			stopSelf()
		}
		return START_STICKY
	}

	private suspend fun runIntervals(intervals: List<Int>) {
		intervals.forEachIndexed { index, secondsTotal ->
			var remaining = secondsTotal
			beep()

			while (remaining > 0) {
				updateNotification("Интервал ${index + 1}, осталось $remaining c")
				delay(1000)
				remaining--
				scope.launch {
					getLocation()?.let { if (!listGeoPoint.contains(it)) { listGeoPoint.add(it) } }
				}
			}
			beep()
			if (index == intervals.lastIndex) {
				delay(500)
				beep()
			}
		}
		updateNotification("Готово ✅")
	}

	private fun beep() {
		ToneGenerator(AudioManager.STREAM_MUSIC, 100)
			.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
	}

	private fun createNotificationChannel() {
		val channel = NotificationChannel(
			CHANNEL_ID,
			"Interval Timer",
			NotificationManager.IMPORTANCE_LOW
		)
		getSystemService(NotificationManager::class.java)
			.createNotificationChannel(channel)
	}

	private fun buildNotification(text: String): Notification {
		val stopIntent = Intent(this, IntervalService::class.java).apply {
			action = ACTION_STOP
		}
		val stopPendingIntent = PendingIntent.getService(
			this,
			0,
			stopIntent,
			PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
		)
		val builder = NotificationCompat.Builder(this, CHANNEL_ID)
			.setContentTitle("Тренировка")
			.setContentText(text)
			.setOngoing(true)
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.addAction(
				R.drawable.ic_launcher_foreground,
				"Стоп",
				stopPendingIntent
			)

		return builder.build()
	}

	private fun updateNotification(text: String) {
		val mgr = getSystemService(NotificationManager::class.java)
		mgr.notify(NOTIFICATION_ID, buildNotification(text))
	}

	override fun onDestroy() {
		super.onDestroy()
		App.setRunningService(false)
		scope.cancel()
	}

	@SuppressLint("MissingPermission")
	private suspend fun getLocation(): GeoPoint? {
		val fused = LocationServices.getFusedLocationProviderClient(this)

		return suspendCancellableCoroutine { cont ->
			val tokenSource = CancellationTokenSource()

			fused.getCurrentLocation(
				Priority.PRIORITY_HIGH_ACCURACY,
				tokenSource.token
			)
				.addOnSuccessListener { location ->
					cont.resume(location?.let { GeoPoint(it) }) { _, _, _ -> }
				}
				.addOnFailureListener {
					cont.resume(null) { _, _, _ -> }
				}

			cont.invokeOnCancellation {
				tokenSource.cancel()
			}
		}
	}

	private suspend fun saveTrack() {
		val json = listGeoPoint.toJson()
		preferencesManager.setListGeoPoint(json)
		listGeoPoint.clear()
	}

	companion object {
		private const val CHANNEL_ID = "interval_channel"
		private const val NOTIFICATION_ID = 1
		const val EXTRA_INTERVALS = "extra_intervals"
		const val ACTION_STOP = "com.kognitivist.trainingtracker.action.STOP"
	}
}

