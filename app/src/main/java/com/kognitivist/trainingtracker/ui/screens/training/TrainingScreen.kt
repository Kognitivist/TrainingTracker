package com.kognitivist.trainingtracker.ui.screens.training

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kognitivist.trainingtracker.app.App
import com.kognitivist.trainingtracker.data.pref_manager.PreferencesManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun TrainingScreen(
	state: TrainingScreenState,
	changeState: (TrainingScreenState) -> Unit,
	onStart: () -> Unit,
	onStop: () -> Unit,
	onBack: () -> Unit
) {
	val isRunningService = App.isRunningService.collectAsStateWithLifecycle().value
	val context = LocalContext.current
	val preferencesManager = remember { PreferencesManager(context) }
	val trackPointsJson by preferencesManager.currentListGeoPointFlow
		.collectAsStateWithLifecycle("[]")
	val trackPoints = remember(trackPointsJson) {
		trackPointsJson.toListGeoPoint()
	}

	LaunchedEffect(trackPoints) {
		if (trackPoints.isNotEmpty()){
			changeState(state.copy(isTimer = false))
		}
		Log.d("SerGeoTag", "trackPoints $trackPoints")
	}

	BackHandler() { onBack() }
	Box(
		modifier = Modifier
			.fillMaxSize()
			.windowInsetsPadding(WindowInsets.statusBars)
	) {
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			item {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.clickable {
							changeState(state.copy(isTimer = true))
						}
					) {
						Icon(
							imageVector = if (state.isTimer) Icons.Filled.AccessTimeFilled else Icons.Outlined.AccessTime,
							null,
						)
						Spacer(Modifier.width(16.dp))
						Text("Timer")
					}
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.clickable {
							if (trackPoints.isNotEmpty()){
								changeState(state.copy(isTimer = false))
							}else{
								Toast.makeText(context, "Трек не записан", Toast.LENGTH_SHORT).show()
							}
						}
					) {
						Text("Map")
						Spacer(Modifier.width(16.dp))
						Icon(
							imageVector = if (state.isTimer) Icons.Outlined.Map else Icons.Filled.Map,
							null,
						)
					}

				}
			}
			if (state.isTimer) {
				items(state.intervals) {
					IntervalItem(it, state.intervals.max())
				}
				item {
					Text("sum: ${state.sum}")
				}
			}
			else {
				item {
					Map(trackPoints)
				}
			}
		}
		if (state.isTimer){
			Button(
				onClick = if (isRunningService) onStop else onStart,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 64.dp)
			) {
				Text(text = if (isRunningService) "Стоп" else "Старт")
			}
		}
	}

}

@Composable
fun IntervalItem(interval: Int, max: Int) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(48.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = interval.toString(),
			modifier = Modifier.width(50.dp),
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.width(8.dp))
		Card(
			modifier = Modifier
				.fillMaxWidth((interval.toFloat() / max.toFloat()))
				.height(32.dp),
			shape = RoundedCornerShape(4.dp),
			colors = CardDefaults.cardColors(containerColor = Color.Blue)
		) {}
	}
}

@Composable
fun Map(trackPoints: List<GeoPoint>) {
	val context = LocalContext.current
	val mapView = remember {
		MapView(context).apply {
			setTileSource(TileSourceFactory.MAPNIK)
			setMultiTouchControls(true)
		}
	}

	LaunchedEffect(Unit) {
		Configuration.getInstance().userAgentValue = context.packageName

		if (trackPoints.isNotEmpty()) {
			val startPoint = trackPoints.first()
			val controller = mapView.controller
			val distanceMeters = if (trackPoints.size >= 2) {
				startPoint.distanceToAsDouble(trackPoints.last())
			} else 0.0

			mapView.overlays.clear()

			if (distanceMeters < 1.0) {
				val circle = FolderOverlay()
				val marker = Marker(mapView).apply {
					position = startPoint
					setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
				}

				circle.add(marker)
				mapView.overlays.add(circle)

				controller.setZoom(19.0)
				controller.setCenter(startPoint)

			} else {
				val polyline = Polyline().apply {
					setPoints(trackPoints)
					outlinePaint.color = 0xFFFF0000.toInt()
					outlinePaint.strokeWidth = 8f
				}
				mapView.overlays.add(polyline)
				controller.setZoom(18.0)
				controller.setCenter(startPoint)
			}
			mapView.invalidate()
		}
	}

	DisposableEffect(Unit) {
		mapView.onResume()
		onDispose {
			mapView.onPause()
			mapView.onDetach()
		}
	}

	AndroidView(
		modifier = Modifier
			.fillMaxWidth()
			.aspectRatio(1.5f)
			.padding(top = 100.dp),
		factory = { mapView },
		update = { }
	)

}

@Stable
data class TrainingScreenState(
	val intervals: List<Int> = listOf(),
	val sum: Int = 0,
	val isTimer: Boolean = true
)

fun List<GeoPoint>.toJson(): String {
	Log.d("SerGeoTag","toJson $this")
	return try {
		Log.d("SerGeoTag","toJsonResult ${Gson().toJson(this)}")
		Gson().toJson(this)
	}catch (e: Exception){
		""
	}
}

fun String.toListGeoPoint(): List<GeoPoint> {
	if (this.isBlank()) { return emptyList() }

	Log.d("SerGeoTag","toList $this")
	val type = object : TypeToken<List<GeoPoint>>(){}.type
	return try {
		val result = Gson().fromJson<List<GeoPoint>>(this, type)
		Log.d("SerGeoTag", "toListResult $result")
		return result
	}catch (e: Exception){
		Log.d("SerGeoTag", "error ${e}")
		listOf()
	}
}